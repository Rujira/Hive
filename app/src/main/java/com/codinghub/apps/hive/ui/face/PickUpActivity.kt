package com.codinghub.apps.hive.ui.face

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentResponse
import com.codinghub.apps.hive.model.identifyparent.ParentStudentData
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.notifyparent.NotifyParentResponse
import com.codinghub.apps.hive.model.utilities.SafeClickListener
import com.codinghub.apps.hive.viewmodel.FaceViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_pick_up.*
import java.math.RoundingMode
import java.text.DecimalFormat

class PickUpActivity : AppCompatActivity(), ParentStudentAdapter.ParentStudentRecyclerViewClickListener {

    private lateinit var faceViewModel: FaceViewModel

    private val listener: OnParentStudentItemInteractionListener? = null

    interface OnParentStudentItemInteractionListener {
        fun onParentStudentItemClicked(studentData: ParentStudentData, state: Int)
    }
    private lateinit var loadingDialog: AlertDialog

    private var tracker: SelectionTracker<Long>? = null
    private var selectedStudentPID = arrayListOf<String>()

    lateinit var identifyParentResponse: IdentifyParentResponse

    private val TAG = PickUpActivity::class.qualifiedName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pick_up)

        faceViewModel = ViewModelProviders.of(this).get(FaceViewModel::class.java)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f
        disableSendConfirmationButton()

        this.title = getString(R.string.parent_pick_up_title)

        identifyParentResponse = intent.getParcelableExtra(FaceFragment.PICK_UP_INTENT_LIST_KEY)

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getImagePath()}${AppPrefs.getSchoolID()}/parent/${identifyParentResponse.image}")
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .into(parentPickUpImageView)
        parentPickUpNameTextView.text = identifyParentResponse.name
        parentPickUpIdTextView.text = getString(R.string.parent_id, identifyParentResponse.parent_pid)

        val df = DecimalFormat("##.##")
        df.roundingMode = RoundingMode.CEILING
        parentPickUpSimilarityTextView.text = getString(R.string.similarity_string, df.format(identifyParentResponse.similarity))

        val studentData: List<ParentStudentData> = identifyParentResponse.students
        val adapter = ParentStudentAdapter(studentData, this)

        psRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        psRecyclerView.adapter = adapter

        tracker = SelectionTracker.Builder<Long>(
            "selection-1",
            psRecyclerView,
            StableIdKeyProvider(psRecyclerView),
            ParentStudentAdapter.MyLookup(psRecyclerView),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>(){
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val nItems:Int? = tracker?.selection?.size()

                if (nItems != null && nItems > 0) {
                    enableSendConfirmationButton()
                } else {
                    disableSendConfirmationButton()
                }
            }
        })

        adapter.setTracker(tracker)

        sendConfirmationButton.setSafeOnClickListener {

            sendConfirmationMessage()
        }
    }

    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
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

    private fun disableSendConfirmationButton() {
        sendConfirmationButton.isEnabled = false
    }

    private fun enableSendConfirmationButton() {
        sendConfirmationButton.isEnabled = true
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun parentStudentItemClicked(studentData: ParentStudentData, state: Int) {
        listener?.onParentStudentItemClicked(studentData, state)

        when(state) {
            0 -> {
                Log.d(TAG, "Deselect StudentData: ${studentData}")
                selectedStudentPID.remove(studentData.student_pid.toString())
            }
            1 -> {
                Log.d(TAG, "Select StudentData: ${studentData}")
                selectedStudentPID.add(studentData.student_pid.toString())
            }
        }

        Log.d(TAG, "Result: ${selectedStudentPID}")

    }

    private fun sendConfirmationMessage() {

        showLoadingDialogWith("Sending Confirmation...")
        faceViewModel.notifyParent(AppPrefs.getSchoolID().toString(),
            identifyParentResponse.parent_pid,
            identifyParentResponse.search_log_id,
            selectedStudentPID).observe(this, Observer<Either<NotifyParentResponse>> { either ->

            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0) {

                    Toast.makeText(this,"Send confirmation successful.", Toast.LENGTH_LONG).show()
                    finish()
                } else {

                    Toast.makeText(this,"Error send confirmation.", Toast.LENGTH_SHORT).show()
                }

            } else {
                if (either?.error == ApiError.NOTIFY_PARENT) {
                    Toast.makeText(this,"Error send confirmation.", Toast.LENGTH_SHORT).show()
                }
            }
            dismissLoadingDialog()
        })
    }



}
