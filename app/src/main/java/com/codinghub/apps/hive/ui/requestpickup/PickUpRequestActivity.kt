package com.codinghub.apps.hive.ui.requestpickup

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Observer
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.pickuprequest.PickUpResponse
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.codinghub.apps.hive.model.utilities.SafeClickListener
import com.codinghub.apps.hive.ui.students.legacy.StudentDetailsActivity
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_request_pick_up.*
import kotlinx.android.synthetic.main.choose_image_sheet.view.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class PickUpRequestActivity : AppCompatActivity() {

    private lateinit var studentsViewModel: StudentsViewModel

    private var studentsToRequest = mutableListOf<NewStudentData>()
    private var studentsPidToRequest = mutableListOf<String>()

    private var isTakePhoto : Boolean = false

    private var image_uri: Uri? = null
    private var exif_data: Uri? = null

    private lateinit var snapImage: Bitmap

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_GALLERY_CODE = 1002

    private lateinit var loadingDialog: AlertDialog

    private val TAG = PickUpRequestActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_pick_up)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        this.title = getString(R.string.request_title)
        studentsToRequest.add(intent.getParcelableExtra(StudentDetailsActivity.INTENT_PICK_UP_REQUEST_KEY))
        Log.d(TAG, "StudentData : ${studentsToRequest}")

        for (student in studentsToRequest) {
            studentsPidToRequest.add(student.student_pid.toString())
        }

        val adapter = PickUpRequestAdapter(studentsToRequest)
        pickUpRequestRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        pickUpRequestRecyclerView.adapter = adapter

        contactNameTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateUI()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        pickupRequestImageView.setSafeOnClickListener {
            showChooseImageDialog()
        }

        sendPickUpRequestButton.setSafeOnClickListener {

            sendPickUpRequest()
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }

    private fun updateUI() {
        if(contactNameTextView.text!!.isNotEmpty() && isTakePhoto) {
            enableSendPickUpRequestButton()
        } else {
            disableSendPickUpRequestButton()
        }
    }

    private fun disableSendPickUpRequestButton() {
        sendPickUpRequestButton.isEnabled = false
    }

    private fun enableSendPickUpRequestButton() {
        sendPickUpRequestButton.isEnabled = true
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

    private fun sendPickUpRequest() {

        if (isTakePhoto) {

            showLoadingDialogWith("Sending Request...")

            val base64 = getImageBase64(pickupRequestImageView)
            studentsViewModel.requestPickUp(base64,
                contactNameTextView.text.toString(), AppPrefs.getSchoolID().toString(),
                studentsPidToRequest).observe(this, Observer<Either<PickUpResponse>>  { either ->

                if (either?.status == Status.SUCCESS && either.data != null) {
                    if (either.data.ret == 0) {

                        Toast.makeText(this,"Send request successful.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {

                        Toast.makeText(this,"Error to send request.", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    if (either?.error == ApiError.NOTIFY_PARENT) {
                        Toast.makeText(this,"Error to send request.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this,"Error to send request.", Toast.LENGTH_SHORT).show()
                    }
                }
                dismissLoadingDialog()
            })
        } else {
            Toast.makeText(this, "Please take a photo", Toast.LENGTH_LONG).show()
        }
    }

    private fun showChooseImageDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = this.layoutInflater.inflate(R.layout.choose_image_sheet, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        val dialog = dialogBuilder.create()

        dialogView.textViewCamera.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    openCamera()
                    dialog.dismiss()
                }

            } else {
                openCamera()
                dialog.dismiss()
            }

        }
        dialogView.textViewGallery.setOnClickListener {
            openGallery()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, IMAGE_GALLERY_CODE)
    }

    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK) {
            isTakePhoto = true

            val ins : InputStream? = contentResolver.openInputStream(image_uri)
            val snapImage : Bitmap? = BitmapFactory.decodeStream(ins)
            ins?.close()


            if (snapImage != null) {
                pickupRequestImageView.setImageBitmap(studentsViewModel.modifyImageOrientation(this ,snapImage, image_uri!!))

            }

        }

        else if (requestCode == IMAGE_GALLERY_CODE && resultCode == Activity.RESULT_OK) {

            isTakePhoto = true

            exif_data = data?.data!!
            val ins: InputStream? = contentResolver.openInputStream(exif_data)
            snapImage = BitmapFactory.decodeStream(ins)

            pickupRequestImageView.setImageBitmap(studentsViewModel.modifyImageOrientation(this ,snapImage, exif_data!!))
        }

        updateUI()
    }

    private fun getImageBase64(image: ImageView): String {

        val bitmap = (image.drawable as BitmapDrawable).bitmap

       // val resizedBitmap = resizeBitmap(bitmap,800,1066)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()

        var base64String: String

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            Log.d(TAG, "Android O")
            base64String = Base64.getEncoder().encodeToString(byteArray)

        } else {
            Log.d(TAG, "Android Other")
            base64String = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
        }
        return base64String
    }

    private fun resizeBitmap(bitmap: Bitmap, width:Int, height:Int): Bitmap {

        return Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            false
        )
    }






}
