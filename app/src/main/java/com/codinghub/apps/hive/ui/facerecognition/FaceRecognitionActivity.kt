package com.codinghub.apps.hive.ui.facerecognition

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.kioskutilities.camera.CameraSourcePreview
import com.codinghub.apps.hive.kioskutilities.camera.GraphicOverlay
import com.codinghub.apps.hive.kioskutilities.facedetection.GraphicFaceTracker
import com.codinghub.apps.hive.kioskutilities.facedetection.GraphicFaceTrackerFactory
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentResponse
import com.codinghub.apps.hive.model.identifyparent.ParentStudentData
import com.codinghub.apps.hive.model.notifyparent.NotifyParentResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.utilities.Exif
import com.codinghub.apps.hive.model.utilities.SafeClickListener
import com.codinghub.apps.hive.ui.face.FaceFragment
import com.codinghub.apps.hive.ui.face.PickUpActivity
import com.codinghub.apps.hive.ui.myaccount.MyStudentAdapter
import com.codinghub.apps.hive.viewmodel.FaceViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.material.snackbar.Snackbar
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_face_recognition.*
import kotlinx.android.synthetic.main.fragment_face.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

class FaceRecognitionActivity : AppCompatActivity(), GraphicFaceTracker.GraphicFaceTrackerListener, GraphicFaceTracker.GraphicFaceTrackerDismissListener {

    //    Let's say the supported camera preview sizes by device are:
//
//    1280 720, 1024 576, 960 720, 800 600, 800 480, 768 576, 736 552, etc..
//
//    It's obvious that there's mismatch between these sizes and standard texture sizes 2048x2048, 2048x1024, 1024x1024...

    private val listener: OnFaceSnappedListener? = null

    interface OnFaceSnappedListener {
        fun faceSnapped()
    }

    private val dismissListener: OnDialogFaceDismissListener? = null
    interface OnDialogFaceDismissListener {
        fun dismissDialog()
    }

    private lateinit var faceViewModel: FaceViewModel

    private val MAX_PREVIEW_WIDTH = 1024
    private val MAX_PREVIEW_HEIGHT = 576

    private var mCameraSource: CameraSource? = null
    private var mPreview: CameraSourcePreview? = null
    private var mGraphicOverlay: GraphicOverlay? = null

    private val TAG = FaceRecognitionActivity::class.qualifiedName

    private lateinit var loadingDialog: AlertDialog

    private lateinit var snapImage: Bitmap

    private var isTakingPhoto: Boolean = false

    private var picture: String = ""

    lateinit var matchDialog: AlertDialog

    companion object {
        private val TAG = "FaceTracker"
        private val RC_HANDLE_GMS = 9001
        private val RC_HANDLE_CAMERA_PERM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)

        this.title = getString(R.string.menu_face_recognition)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        faceViewModel = ViewModelProviders.of(this).get(FaceViewModel::class.java)


        mPreview = findViewById<View>(R.id.preview) as CameraSourcePreview
        mGraphicOverlay = findViewById<View>(R.id.faceOverlay) as GraphicOverlay
        val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(CameraSource.CAMERA_FACING_BACK)
        } else {
            requestCameraPermission()
        }

        changeCameraButton.setSafeOnClickListener {
            if (mCameraSource?.cameraFacing == CameraSource.CAMERA_FACING_FRONT) {
                changeCameraButton.text = "FRONT"
                if (mCameraSource != null) {
                    mCameraSource!!.release()
                }
                createCameraSource(CameraSource.CAMERA_FACING_BACK)
            } else {
                changeCameraButton.text = "BACK"
                if (mCameraSource != null) {
                    mCameraSource!!.release()
                }
                createCameraSource(CameraSource.CAMERA_FACING_FRONT)
            }
            startCameraSource()
        }

        identifyButton.setSafeOnClickListener {
            captureImage()
        }

        updateUI()
    }

    private fun updateUI() {
        if (AppPrefs.getKioskAutoSnapMode()) {
            autoSnapButton.visibility = View.VISIBLE
            identifyButton.visibility = View.GONE
        } else {
            autoSnapButton.visibility = View.GONE
            identifyButton.visibility = View.VISIBLE
        }
    }

    override fun faceSnapped() {
        listener?.faceSnapped()
        Log.d(TAG, "Snapped")
        captureImage()
    }

    override fun dismissDialog() {
        dismissListener?.dismissDialog()

        if (matchDialog.isShowing) {
            matchDialog.dismiss()
        }
    }

    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission")
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions,
                RC_HANDLE_CAMERA_PERM
            )
            return
        }

        val thisActivity = this
        val listener = View.OnClickListener {
            ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM
            )
        }

        Snackbar.make(mGraphicOverlay!!, R.string.permission_camera_rationale,
            Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok, listener)
            .show()
    }

    private fun createCameraSource(cameraFacing: Int) {

        val context = applicationContext
        val detector = FaceDetector.Builder(context)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .setMode(FaceDetector.ACCURATE_MODE)
            .build()
        detector.setProcessor(
            MultiProcessor.Builder(mGraphicOverlay?.let {
                GraphicFaceTrackerFactory(
                    it, this
                )
            })
                .build())

        if (!detector.isOperational) {
            Log.w(TAG, "Face detector dependencies are not yet available.")
        }

        mCameraSource = CameraSource.Builder(context, detector)
            .setRequestedPreviewSize(MAX_PREVIEW_WIDTH, MAX_PREVIEW_HEIGHT)
            .setFacing(cameraFacing)
            .setRequestedFps(60.0f)
            .setAutoFocusEnabled(true)
            .build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onPause() {
        super.onPause()
        mPreview!!.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mCameraSource != null) {
            mCameraSource!!.release()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            createCameraSource(CameraSource.CAMERA_FACING_BACK)
            return
        }

        Log.e(
            TAG, "Permission not granted: results len = " + grantResults.size +
                    " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)")

        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Face Tracker sample")
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(R.string.ok, listener)
            .show()
    }

    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }

        if (mCameraSource != null) {
            try {
                mGraphicOverlay?.let { mPreview!!.start(mCameraSource!!, it) }
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                mCameraSource!!.release()
                mCameraSource = null
            }
        }
    }

    private fun captureImage() {
        Log.d(TAG, "Capture")

        if (!isTakingPhoto) {
            isTakingPhoto = true
            mCameraSource?.takePicture(null, CameraSource.PictureCallback { data ->
                Log.d(TAG, "PictureCallback")

                //snapImage = BitmapFactory.decodeByteArray(data, 0, data.size)
                // testImageView.setImageBitmap(bitmap)


                //val ins: InputStream? = contentResolver.openInputStream(data)

                val orientation = Exif.getOrientation(data)
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)

                snapImage = when (orientation) {
                    90 -> rotateImage(bitmap, 90f)
                    180 -> rotateImage(bitmap, 180f)
                    270 -> rotateImage(bitmap, 270f)
                    else -> rotateImage(bitmap, 0f)
                }

                val stream = ByteArrayOutputStream()
                snapImage.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val byteArray = stream.toByteArray()

              //  var base64String: String

                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                    Log.d(TAG, "Android O")
                    picture = Base64.getEncoder().encodeToString(byteArray)

                } else {
                    Log.d(TAG, "Android Other")
                    picture = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
                }

//                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
//                    Log.d(TAG, "Android O")
//                    picture = Base64.getEncoder().encodeToString(data)
//
//                } else {
//                    Log.d(TAG, "Android Other")
//                    picture = android.util.Base64.encodeToString(data, android.util.Base64.NO_WRAP)
//                }

                identifyFace()
            })
        }
    }

    private fun rotateImage(bitmap: Bitmap, angle: Float): Bitmap {
        val mat : Matrix? = Matrix()
        mat?.postRotate(angle)
        return Bitmap.createBitmap(bitmap , 0, 0, bitmap.width,
            bitmap.height, mat, true)
    }


    private fun identifyFace() {

        showLoadingDialogWith("Identifying Face...")

        faceViewModel.identifyParent(picture, AppPrefs.getSchoolID().toString())
            .observe(this, Observer<Either<IdentifyParentResponse>> { either ->
                if (either?.status == Status.SUCCESS && either.data != null) {
                    if (either.data.ret == 0 && either.data.similarity > 90) {
                        Log.d(TAG,  "Data ${either.data}")
                        if (AppPrefs.getKioskAutoSnapMode()) {
                            showMatchDialog(either.data, "")

                        } else {
                            showResult(either.data)
                            isTakingPhoto = false
                        }

                    } else {
                        showMatchDialog(null, getString(R.string.face_not_found))
                    }
                } else {
                    if (either?.error == ApiError.IDENTIFY_PARENT) {
                        showMatchDialog(null, "Could not retrieving information.")
//                        if (AppPrefs.getKioskAutoSnapMode()) {
//
//                        } else {
//                           // Toast.makeText(this, getString(R.string.error_retrieving_information), Toast.LENGTH_SHORT).show()
//                        }
                    }
                   // isTakingPhoto = false
                }
                dismissLoadingDialog()

            })
    }

    private fun showLoadingDialogWith(message: String){

        loadingDialog = SpotsDialog.Builder()
            .setContext(this)
            .setMessage(message)
            .setCancelable(false)
            .build()
            .apply {
                show()
            }
    }

    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    private fun showResult(identifyParentResponse: IdentifyParentResponse) {

        val pickUpIntent = Intent(this, PickUpActivity::class.java)
        pickUpIntent.putExtra(FaceFragment.PICK_UP_INTENT_LIST_KEY, identifyParentResponse)
        startActivityForResult(pickUpIntent, FaceFragment.PARENT_DETAIL_REQUEST_CODE)

    }

    private fun showMatchDialog(identifyParentResponse: IdentifyParentResponse?, message: String) {

        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = this.layoutInflater.inflate(R.layout.dialog_match, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)

        val snapImageView = dialogView.findViewById<ImageView>(R.id.snapImageView)
        val nameTextView = dialogView.findViewById<TextView>(R.id.nameTextView)
        val similarityTextView = dialogView.findViewById<TextView>(R.id.similarityTextView)
        val pidTextView = dialogView.findViewById<TextView>(R.id.pidTextView)
        val symbolImageView = dialogView.findViewById<ImageView>(R.id.symbolImageView)
        val bgMatchImageViewBG1 = dialogView.findViewById<ImageView>(R.id.bgMatchImageViewBG1)
        val alertTextView = dialogView.findViewById<TextView>(R.id.alertTextView)
        val studentRecyclerView = dialogView.findViewById<RecyclerView>(R.id.studentRecyclerView)

        if (identifyParentResponse != null) {

            if (identifyParentResponse.similarity > 90) {

                snapImageView.visibility = View.VISIBLE
                nameTextView.visibility = View.VISIBLE
                similarityTextView.visibility = View.VISIBLE
                pidTextView.visibility = View.VISIBLE
                symbolImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_match))
                bgMatchImageViewBG1.setColorFilter(ContextCompat.getColor(this, R.color.successColor))
                alertTextView.visibility = View.INVISIBLE
                studentRecyclerView.visibility = View.VISIBLE

                snapImageView.setImageBitmap(snapImage)

                nameTextView.text = identifyParentResponse.name

                val df = DecimalFormat("##.##")
                df.roundingMode = RoundingMode.CEILING

                similarityTextView.text = "Similarity : " + df.format(identifyParentResponse.similarity) + "%"

                pidTextView.text = identifyParentResponse.parent_pid

                studentRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                studentRecyclerView.adapter = FaceRecognitionStudentAdapter(identifyParentResponse.students)

                var selectedStudentPID = arrayListOf<String>()
                for (student in identifyParentResponse.students) {
                    selectedStudentPID.add(student.student_pid.toString())
                }

                sendConfirmationMessage(identifyParentResponse.parent_pid, identifyParentResponse.search_log_id, selectedStudentPID)


            } else {
                snapImageView.visibility = View.INVISIBLE
                nameTextView.visibility = View.INVISIBLE
                similarityTextView.visibility = View.INVISIBLE
                pidTextView.visibility = View.INVISIBLE
                symbolImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mismatch))
                bgMatchImageViewBG1.setColorFilter(ContextCompat.getColor(this, R.color.warningColor))
                alertTextView.visibility = View.VISIBLE
                alertTextView.text = message
            }

        } else {
            snapImageView.visibility = View.INVISIBLE
            nameTextView.visibility = View.INVISIBLE
            similarityTextView.visibility = View.INVISIBLE
            pidTextView.visibility = View.INVISIBLE
            symbolImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mismatch))
            bgMatchImageViewBG1.setColorFilter(ContextCompat.getColor(this, R.color.warningColor))
            alertTextView.visibility = View.VISIBLE
            alertTextView.text = message
        }

        matchDialog = dialogBuilder.create()

        matchDialog.show()

        matchDialog.setOnDismissListener {
            isTakingPhoto = false
        }

    }

    private fun sendConfirmationMessage(pid: String, searchLogId: Int, selectedStudentPID: List<String>) {

       // showLoadingDialogWith("Sending Confirmation...")
        faceViewModel.notifyParent(AppPrefs.getSchoolID().toString(),
            pid,
            searchLogId,
            selectedStudentPID).observe(this, Observer<Either<NotifyParentResponse>> { either ->

            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0) {

                    Toast.makeText(this,"Send confirmation successful.", Toast.LENGTH_LONG).show()
                //    finish()
                } else {

                    Toast.makeText(this,"Error send confirmation.", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (either?.error == ApiError.NOTIFY_PARENT) {
                    Toast.makeText(this,"Error send confirmation.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


}
