package com.codinghub.apps.hive.ui.notifications.messages


import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.notifications_message.MessageResponse
import com.codinghub.apps.hive.model.notifications_message.MessageData
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.model.notifications_message.parent.ParentMessageResponse
import com.codinghub.apps.hive.model.notifications_message.teacher.TeacherMessageRequest
import com.codinghub.apps.hive.model.notifications_message.teacher.TeacherMessageResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.viewmodel.NotificationsViewModel
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_messages_list.*
import kotlinx.android.synthetic.main.fragment_messages_list.view.*

class MessageListFragment : Fragment(), MessageListAdapter.MessageListRecyclerViewClickListener {

    private val listener: OnMessageItemInteractionListener? = null

    interface OnMessageItemInteractionListener {
        fun onMessageItemClicked(message: MessageData)
    }

    private lateinit var notificationsViewModel: NotificationsViewModel

    private lateinit var emptyMessageLayout: ConstraintLayout

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val TAG = MessageListFragment::class.qualifiedName

    lateinit var messageDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages_list, container, false)

        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)

        view.swipeRefreshLayout.setOnRefreshListener {
            configureUI(notificationsViewModel.getCurrentUser().first(), true)

        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.let {
            emptyMessageLayout = it.findViewById(R.id.emptyMessageLayout)
            swipeRefreshLayout = it.findViewById(R.id.swipeRefreshLayout)
            emptyMessageLayout.visibility = View.INVISIBLE

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureUI(notificationsViewModel.getCurrentUser().first(), false)

    }

    private fun configureUI(currentUser: CurrentUser, isOnRefresh: Boolean) {

        when(currentUser.role) {
            UserRole.ADMIN -> {

                notificationsViewModel.getMessagesForTeacher(AppPrefs.getCurrentUser().first().userId, AppPrefs.getCurrentUser().first().role.rolename, AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<TeacherMessageResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                            emptyMessageLayout.visibility = View.GONE
                            val messageList: List<MessageData> = either.data.data

                            Log.d(TAG, "MessageResponse : ${messageList}")

                            messageRecyclerView.layoutManager = LinearLayoutManager(context)
                            messageRecyclerView.adapter = MessageListAdapter(messageList, this)
                            (messageRecyclerView.adapter as MessageListAdapter).notifyDataSetChanged()

                        } else {
                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                            emptyMessageLayout.visibility = View.VISIBLE
                            messageRecyclerView.visibility = View.INVISIBLE
                            // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.GET_PICK_UP_MESSAGE) {

                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                            //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        } else {
                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                        }
                        emptyMessageLayout.visibility = View.VISIBLE
                        messageRecyclerView.visibility = View.INVISIBLE
                    }
                })

            }

            UserRole.TEACHER -> {

                notificationsViewModel.getMessagesForTeacher(AppPrefs.getCurrentUser().first().userId, AppPrefs.getCurrentUser().first().role.rolename, AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<TeacherMessageResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                            emptyMessageLayout.visibility = View.GONE
                            val messageList: List<MessageData> = either.data.data

                            Log.d(TAG, "MessageResponse : ${messageList}")

                            messageRecyclerView.layoutManager = LinearLayoutManager(context)
                            messageRecyclerView.adapter = MessageListAdapter(messageList, this)
                            (messageRecyclerView.adapter as MessageListAdapter).notifyDataSetChanged()

                        } else {
                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                            emptyMessageLayout.visibility = View.VISIBLE
                            messageRecyclerView.visibility = View.INVISIBLE
                            // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.GET_PICK_UP_MESSAGE) {

                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                            //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        } else {
                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                        }
                        emptyMessageLayout.visibility = View.VISIBLE
                        messageRecyclerView.visibility = View.INVISIBLE
                    }
                })
            }

            UserRole.PARENT -> {

                val pid = notificationsViewModel.getCurrentUser().first().userId
                notificationsViewModel.getMessagesForParent(AppPrefs.getCurrentUser().first().userId, AppPrefs.getCurrentUser().first().role.rolename, AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<ParentMessageResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                            emptyMessageLayout.visibility = View.GONE
                            val messageList: List<MessageData> = either.data.data

                            Log.d(TAG, "MessageResponse : ${messageList}")
                            messageRecyclerView.layoutManager = LinearLayoutManager(context)
                            messageRecyclerView.adapter = MessageListAdapter(messageList, this)
                            (messageRecyclerView.adapter as MessageListAdapter).notifyDataSetChanged()

                        } else {
                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                            emptyMessageLayout.visibility = View.VISIBLE
                            messageRecyclerView.visibility = View.INVISIBLE
                           // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.GET_PICK_UP_MESSAGE) {

                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)

                          //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        } else {
                            messagesTitleTextView.text = getString(R.string.messages_empty_title)
                            messagesSubtitleTextView.text = getString(R.string.messages_empty_subtitle)
                        }
                        emptyMessageLayout.visibility = View.VISIBLE
                        messageRecyclerView.visibility = View.INVISIBLE
                    }
                })
            }
        }
        if (isOnRefresh) {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun messageItemClicked(message: MessageData) {
        listener?.onMessageItemClicked(message)
        showMessageDialog(message)
    }

    private fun showMessageDialog(message: MessageData) {

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

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getImagePath()}${AppPrefs.getSchoolID()}/log/${message.image}").into(requestImageView)
        requestTitleTextView.text = "Notify Message"
        requestDetailTextView.text = "This person've come to get ${message.student_name}."
        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/student/${message.student_image}").into(requestStudentImageView)

        dialogBuilder.setNegativeButton("Dismiss") { _, _->
            //pass
        }

        messageDialog = dialogBuilder.create()
        messageDialog.show()

        val dismissButton = messageDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        dismissButton.setBackgroundColor(Color.TRANSPARENT)
        dismissButton.setTextColor(ContextCompat.getColor(activity!!.applicationContext,R.color.colorSecondaryPurple))

        chipAllow.visibility = View.GONE
        chipNotAllow.visibility = View.GONE

    }
}
