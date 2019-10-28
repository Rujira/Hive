package com.codinghub.apps.hive.ui.myaccount

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
import androidx.lifecycle.Observer
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.myaccount.parent.AddSenderResponse
import com.codinghub.apps.hive.model.myaccount.parent.ParentInfoData
import com.codinghub.apps.hive.model.myaccount.parent.UpdateParentResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.utilities.SafeClickListener
import com.codinghub.apps.hive.viewmodel.MyAccountViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_update_profile.*
import kotlinx.android.synthetic.main.choose_image_sheet.view.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {

    private var isTakePhoto : Boolean = false

    private var image_uri: Uri? = null
    private var exif_data: Uri? = null

    private lateinit var snapImage: Bitmap

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private val IMAGE_GALLERY_CODE = 1002

    private lateinit var myAccountViewModel: MyAccountViewModel

    private lateinit var loadingDialog: AlertDialog

    private val TAG = UpdateProfileActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        myAccountViewModel = ViewModelProviders.of(this).get(MyAccountViewModel::class.java)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        val parentInfoData = intent.getParcelableExtra<ParentInfoData>(MyAccountFragment.UPDATE_PROFILE_KEY)

        if (intent.getStringExtra(MyAccountFragment.UPDATE_PROFILE_FLAG) == "main") {

            this.title = "Update Profile"

            fullnameTextView.setText(parentInfoData.name)
            telTextView.setText(parentInfoData.tel)

            Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getImagePath()}${AppPrefs.getSchoolID()}/parent/${parentInfoData.image}")
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(profileImageView)

            updateProfileButton.setSafeOnClickListener {
                updateProfile(parentInfoData)
            }

        } else {

            this.title = "Add New Sender"

            updateProfileButton.text = "Add New Sender"
            updateProfileButton.setOnClickListener {
                addNewSender(parentInfoData)
            }

        }

        fullnameTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateUI()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        telTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateUI()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        profileImageView.setOnClickListener {
            showChooseImageDialog()
        }

        updateUI()

    }

    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }


    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
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

    private fun updateProfile(parentInfoData: ParentInfoData) {

        showLoadingDialogWith("Updating Profile...")

        myAccountViewModel.updateParentInfo(
            fullnameTextView.text.toString(),
            parentInfoData.parent_pid.toString(),
            getImageBase64(profileImageView),
            AppPrefs.getSchoolID().toString(),
            telTextView.text.toString())
            .observe(this, Observer<Either<UpdateParentResponse>> { either ->
                if (either?.status == Status.SUCCESS && either.data != null) {
                    if (either.data.ret == 0) {
                        Toast.makeText(this, "Update Profile Successful", Toast.LENGTH_SHORT).show()

                        finish()
                    } else {
                        Toast.makeText(this, "Update Profile Failed", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    if (either?.error == ApiError.UPDATE_PARENT_INFO) {
                        Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Could not connect to server", Toast.LENGTH_SHORT).show()
                    }
                }
                dismissLoadingDialog()
            })
    }

    private fun addNewSender(parentInfoData: ParentInfoData) {

        showLoadingDialogWith("Add New Sender...")

        myAccountViewModel.addNewSender(
            fullnameTextView.text.toString(),
            parentInfoData.parent_pid.toString(),
            getImageBase64(profileImageView),
            AppPrefs.getSchoolID().toString(),
            telTextView.text.toString())
            .observe(this,Observer<Either<AddSenderResponse>> { either ->
                if (either?.status == Status.SUCCESS && either.data != null) {
                    if (either.data.ret == 0) {
                        Toast.makeText(this, "Add New Sender Successful", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Add New Sender Failed", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    if (either?.error == ApiError.UPDATE_PARENT_INFO) {
                        Toast.makeText(this, "API Error", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Could not connect to server", Toast.LENGTH_SHORT).show()
                    }
                }
                dismissLoadingDialog()
            })
    }

    private fun updateUI() {
        if(fullnameTextView.text!!.isNotEmpty() && telTextView.text!!.length == 10 && isTakePhoto) {
            enableUpdateProfileButton()
        } else {
            disableUpdateProfileButton()
        }
    }

    private fun disableUpdateProfileButton() {
        updateProfileButton.isEnabled = false
    }

    private fun enableUpdateProfileButton() {
        updateProfileButton.isEnabled = true
    }


    private fun showChooseImageDialog() {

        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = this.layoutInflater.inflate(R.layout.choose_image_sheet, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)
        val dialog = dialogBuilder.create()

        dialogView.textViewCamera.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
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
                profileImageView.setImageBitmap(myAccountViewModel.modifyImageOrientation(this ,snapImage, image_uri!!))

            }

        }

        else if (requestCode == IMAGE_GALLERY_CODE && resultCode == Activity.RESULT_OK) {

            isTakePhoto = true

            exif_data = data?.data!!
            val ins: InputStream? = contentResolver.openInputStream(exif_data)
            snapImage = BitmapFactory.decodeStream(ins)

            profileImageView.setImageBitmap(myAccountViewModel.modifyImageOrientation(this ,snapImage, exif_data!!))
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
