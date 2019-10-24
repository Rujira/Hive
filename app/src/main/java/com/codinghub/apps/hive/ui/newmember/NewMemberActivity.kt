package com.codinghub.apps.hive.ui.newmember

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
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.newmember.NewMemberResponse
import com.codinghub.apps.hive.viewmodel.NewMemberViewModel
import kotlinx.android.synthetic.main.activity_new_member.*
import kotlinx.android.synthetic.main.choose_image_sheet.view.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Status

class NewMemberActivity : AppCompatActivity() {

    private lateinit var newMemberViewModel: NewMemberViewModel

    private var isTakePhoto : Boolean = false

    private var image_uri: Uri? = null
    private var exif_data: Uri? = null

    private lateinit var snapImage: Bitmap

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_GALLERY_CODE = 1002

    private val TAG = NewMemberActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_member)

        newMemberViewModel = ViewModelProviders.of(this).get(NewMemberViewModel::class.java)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        this.title = getString(R.string.add_new_member_title)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        addNewMemberImageView.setOnClickListener {
            showChooseImageDialog()
        }

        addNewMemberButton.setOnClickListener {
            addNewParent()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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
                    // Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
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
                addNewMemberImageView.setImageBitmap(newMemberViewModel.modifyImageOrientation(this ,snapImage, image_uri!!))

                // (findViewById(R.id.imageView) as ImageView).setImageBitmap(pictureTurn(img, uri));
            }

        }

        else if (requestCode == IMAGE_GALLERY_CODE && resultCode == Activity.RESULT_OK) {

            isTakePhoto = true

            exif_data = data?.data!!
            val ins: InputStream? = contentResolver.openInputStream(exif_data)
            snapImage = BitmapFactory.decodeStream(ins)

            addNewMemberImageView.setImageBitmap(newMemberViewModel.modifyImageOrientation(this ,snapImage, exif_data!!))
        }
    }

    private fun getImageBase64(image: ImageView): String {

        val bitmap = (image.drawable as BitmapDrawable).bitmap

        val resizedBitmap = resizeBitmap(bitmap,800,1066)

        val stream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
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

    private fun generatePersonID() : String {
        val pidString = "HSC" + getCurrentDate()
        Log.d(TAG, "Genrate PID : " + pidString)
        return pidString
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val currentDate = sdf.format(Date())
        return currentDate.toString()
    }

    private fun addNewParent() {
//        data class NewMemberRequest(val picture: String,
//                                    val name: String,
//                                    val parent_pid: String,
//                                    val school_id: String,
//                                    val tel: String)

        val base64 = getImageBase64(addNewMemberImageView)

        newMemberViewModel.addNewParent(base64,
            userNameTextView.text.toString(),
            generatePersonID(), "1",
            telTextView.text.toString()).observe(this, Observer<Either<NewMemberResponse>> { either ->

            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0) {

                    Toast.makeText(this,"Train face complete", Toast.LENGTH_LONG).show()

                    finish()
                } else {
                    // emptyStudentLayout.visibility = View.VISIBLE
                    Toast.makeText(this,"Error training face.", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (either?.error == ApiError.NEW_PARENT) {
                    Toast.makeText(this,"Error training face.", Toast.LENGTH_SHORT).show()
                }

            }
        })


    }
}
