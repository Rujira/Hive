package com.codinghub.apps.hive.ui.notifications.requests

import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.model.notification_request.ApprovePickupResponse
import com.codinghub.apps.hive.model.notification_request.RequestData
import com.codinghub.apps.hive.model.notification_request.RequestResponse
import com.codinghub.apps.hive.model.notification_request.RequestStatus
import com.codinghub.apps.hive.model.notification_request.parent.ParentPickupRequestResponse
import com.codinghub.apps.hive.model.notification_request.teacher.TeacherPickupRequestResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.viewmodel.NotificationsViewModel
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_request_list.*
import kotlinx.android.synthetic.main.fragment_request_list.view.*

class RequestListFragment : Fragment(), RequestListAdapter.RequestListRecyclerViewClickListener {

    private val listener: OnRequestItemInteractionListener? = null

    interface OnRequestItemInteractionListener {
        fun onRequestItemClicked(request: RequestData)
    }

    private lateinit var notificationsViewModel: NotificationsViewModel

    private lateinit var emptyRequestLayout: ConstraintLayout

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val TAG = RequestListFragment::class.qualifiedName

    lateinit var confirmDialog: AlertDialog

    val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.getStringExtra("topic") == "REQUEST") {
                loadData(notificationsViewModel.getCurrentUser().first(), false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request_list, container, false)

        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)

        view.swipeRefreshLayout.setOnRefreshListener {
            loadData(notificationsViewModel.getCurrentUser().first(), true)

        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.let {
            emptyRequestLayout = it.findViewById(R.id.emptyRequestLayout)
            swipeRefreshLayout = it.findViewById(R.id.swipeRefreshLayout)
            emptyRequestLayout.visibility = View.INVISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData(notificationsViewModel.getCurrentUser().first(), false)

    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(activity!!.applicationContext).unregisterReceiver(broadCastReceiver)

        super.onPause()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(activity!!.applicationContext).registerReceiver(broadCastReceiver, IntentFilter("notification-broadcast"))
        super.onResume()
    }


    private fun loadData(currentUser: CurrentUser, isOnRefresh: Boolean) {

        when(currentUser.role) {

            UserRole.ADMIN -> {
                Log.d(TAG, "ADMIN")
                notificationsViewModel.getRequestForTeacher(
                    AppPrefs.getCurrentUser().first().userId,
                    AppPrefs.getCurrentUser().first().role.name,
                    AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<TeacherPickupRequestResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                            emptyRequestLayout.visibility = View.INVISIBLE
                            val requestList: MutableList<RequestData> = either.data.data as MutableList<RequestData>

                            Log.d(TAG, "RequestResponse : ${requestList}")

                            requestRecyclerView.layoutManager = LinearLayoutManager(context)
                            requestRecyclerView.adapter = RequestListAdapter(requestList, this)

                        } else {
                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                            emptyRequestLayout.visibility = View.VISIBLE
                            requestRecyclerView.visibility = View.INVISIBLE
                            // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.GET_PICK_UP_REQUEST) {

                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                            //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        } else {
                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                        }
                        emptyRequestLayout.visibility = View.VISIBLE
                        requestRecyclerView.visibility = View.INVISIBLE
                    }
                })
            }

            UserRole.TEACHER -> {

                Log.d(TAG, "ADMIN")
                notificationsViewModel.getRequestForTeacher(
                    AppPrefs.getCurrentUser().first().userId,
                    AppPrefs.getCurrentUser().first().role.rolename,
                    AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<TeacherPickupRequestResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                            emptyRequestLayout.visibility = View.INVISIBLE
                            val requestList: MutableList<RequestData> = either.data.data as MutableList<RequestData>

                            Log.d(TAG, "RequestResponse : ${requestList}")

                            requestRecyclerView.layoutManager = LinearLayoutManager(context)
                            requestRecyclerView.adapter = RequestListAdapter(requestList, this)

                        } else {
                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                            emptyRequestLayout.visibility = View.VISIBLE
                            requestRecyclerView.visibility = View.INVISIBLE
                            // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.GET_PICK_UP_REQUEST) {

                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                            //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        } else {
                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                        }
                        emptyRequestLayout.visibility = View.VISIBLE
                        requestRecyclerView.visibility = View.INVISIBLE
                    }
                })
            }

            UserRole.PARENT -> {
                Log.d(TAG, "Parent")
                val pid = notificationsViewModel.getCurrentUser().first().userId
                notificationsViewModel.getRequestForParent(AppPrefs.getCurrentUser().first().userId,
                    AppPrefs.getCurrentUser().first().role.name,
                    AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<ParentPickupRequestResponse>> { either ->

                    Log.d(TAG, "Either : ${either.data}")
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                            emptyRequestLayout.visibility = View.GONE
                            val requestList: MutableList<RequestData> = either.data.data as MutableList<RequestData>

                            Log.d(TAG, "RequestResponse : ${requestList}")

                            requestRecyclerView.layoutManager = LinearLayoutManager(context)
                            requestRecyclerView.adapter = RequestListAdapter(requestList, this)

                        } else {
                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                            emptyRequestLayout.visibility = View.VISIBLE
                            requestRecyclerView.visibility = View.INVISIBLE


                            // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d(TAG, "4")
                        if (either?.error == ApiError.GET_PICK_UP_REQUEST) {

                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                            //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        } else {
                            requestsTitleTextView.text = getString(R.string.requests_empty_title)
                            requestsSubtitleTextView.text = getString(R.string.requests_empty_subtitle)
                        }
                        emptyRequestLayout.visibility = View.VISIBLE
                        requestRecyclerView.visibility = View.INVISIBLE
                    }
                })
            }
        }

        if (isOnRefresh) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun requestItemClicked(request: RequestData) {
        listener?.onRequestItemClicked(request)
        showConfirmDialog(request)

    }

    private fun showConfirmDialog(request: RequestData) {

        val dialogBuilder = AlertDialog.Builder(activity)
        val dialogView = this.layoutInflater.inflate(R.layout.dialog_confirm_request, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(true)

        val requestImageView = dialogView.findViewById<ImageView>(R.id.requestImageView)
        val requestTitleTextView = dialogView.findViewById<TextView>(R.id.requestTitleTextView)
        val requestDetailTextView = dialogView.findViewById<TextView>(R.id.requestDetailTextView)
        val requestStudentImageView = dialogView.findViewById<ImageView>(R.id.requestStudentImageView)
        val chipAllow = dialogView.findViewById<Chip>(R.id.chipAllow)
        val chipNotAllow = dialogView.findViewById<Chip>(R.id.chipNotAllow)

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getImagePath()}${AppPrefs.getSchoolID()}/log/${request.image}").into(requestImageView)
        requestTitleTextView.text = "Request For Pickup"

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/student/${request.student_image}").into(requestStudentImageView)

        dialogBuilder.setNegativeButton("Dismiss") { _, _->
            //pass
        }

        confirmDialog = dialogBuilder.create()
        confirmDialog.show()

        val userRole = notificationsViewModel.getCurrentUser().first().role

        if (userRole != UserRole.PARENT) {
            chipAllow.visibility = View.GONE
            chipNotAllow.visibility = View.GONE
            requestDetailTextView.text = "This person've come to get ${request.student_name}."

        } else {
            requestDetailTextView.text = "This person've come to get ${request.student_name}. Please approve to allow for pickup or decline."

        }

        val dismissButton = confirmDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        dismissButton.setBackgroundColor(Color.TRANSPARENT)
        dismissButton.setTextColor(ContextCompat.getColor(activity!!.applicationContext,R.color.colorSecondaryPurple))

        chipAllow.setOnClickListener {
            Log.d(TAG,  "${RequestStatus.ALLOW.status}")
            sendApproval(RequestStatus.ALLOW.status, request.id, AppPrefs.getSchoolID().toString())
        }

        chipNotAllow.setOnClickListener {
            Log.d(TAG, "${RequestStatus.NOTALLOW.status}")
            sendApproval(RequestStatus.NOTALLOW.status, request.id, AppPrefs.getSchoolID().toString())
        }
    }

    private fun sendApproval(approve: String, request_id: String, school_id: String){

        notificationsViewModel.approvePickup(approve, request_id, school_id).observe(this, Observer<Either<ApprovePickupResponse>> { either ->
            if(either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0) {
                    confirmDialog.dismiss()
                    loadData(notificationsViewModel.getCurrentUser().first(), false)
                } else {
                    Toast.makeText(context, "This message request is already answer.", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (either?.error == ApiError.APPROVE_PICK_UP_REQUEST) {
                    Toast.makeText(context, R.string.error_retrieving_information, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}

