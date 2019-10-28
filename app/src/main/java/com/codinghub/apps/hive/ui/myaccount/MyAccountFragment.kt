package com.codinghub.apps.hive.ui.myaccount

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.myaccount.parent.ParentInfoData
import com.codinghub.apps.hive.model.myaccount.parent.ParentUserInfoResponse
import com.codinghub.apps.hive.model.myaccount.parent.SenderData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.codinghub.apps.hive.viewmodel.MyAccountViewModel
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.fullnameTextView
import kotlinx.android.synthetic.main.fragment_my_account.telTextView
import kotlinx.android.synthetic.main.fragment_my_account.updateProfileButton
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import com.codinghub.apps.hive.model.utilities.SafeClickListener

class MyAccountFragment : Fragment(), MyStudentAdapter.MyStudentListRecyclerViewClickListener, SenderAdapter.SenderListRecyclerViewClickListener {

    private val listener: OnStudentItemInteractionListener? = null
    private val senderListener: OnSenderItemInteractionListener? = null

    lateinit var parentInfoData: ParentInfoData

    interface OnStudentItemInteractionListener {
        fun onStudentItemClicked(student: NewStudentData)
    }

    interface OnSenderItemInteractionListener {
        fun onSenderItemClicked(sender: SenderData)
    }

    private lateinit var myAccountViewModel: MyAccountViewModel

    private val TAG = MyAccountFragment::class.qualifiedName

    companion object {
        const val UPDATE_PROFILE_KEY = "updateprofilekey"
        const val UPDATE_PROFILE_FLAG = "updateprofileflag"
        const val UPDATE_PROFILE_REQUEST_CODE = 115
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.my_account)

        val view = inflater.inflate(R.layout.fragment_my_account, container, false)

        myAccountViewModel = ViewModelProviders.of(this).get(MyAccountViewModel::class.java)


        view.updateProfileButton.setSafeOnClickListener {
            Log.d(TAG, "Update Proflie")
            val updateProfileIntent = Intent(activity, UpdateProfileActivity::class.java)
            updateProfileIntent.putExtra(UPDATE_PROFILE_KEY, parentInfoData)
            updateProfileIntent.putExtra(UPDATE_PROFILE_FLAG, "main")
            startActivityForResult(updateProfileIntent, UPDATE_PROFILE_REQUEST_CODE)

        }

        view.addSenderButton.setSafeOnClickListener {
            Log.d(TAG, "Add Sender")
            val updateProfileIntent = Intent(activity, UpdateProfileActivity::class.java)
            updateProfileIntent.putExtra(UPDATE_PROFILE_KEY, parentInfoData)
            updateProfileIntent.putExtra(UPDATE_PROFILE_FLAG, "sender")
            startActivityForResult(updateProfileIntent, UPDATE_PROFILE_REQUEST_CODE)
        }

        return view
    }

    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadParentInfoData(AppPrefs.getCurrentUser().first().userId, AppPrefs.getSchoolID().toString())
    }

    override fun onResume() {
        super.onResume()
        loadParentInfoData(AppPrefs.getCurrentUser().first().userId, AppPrefs.getSchoolID().toString())
    }
    private fun loadParentInfoData(parentID: String, schoolID: String) {

        myAccountViewModel.getParentInfo(parentID, schoolID).observe(this, Observer<Either<ParentUserInfoResponse>> { either ->
            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0) {
                    Log.d(TAG, "Parent User Info : ${either.data}")

                    parentInfoData = either.data.parent_info

                    updateProfileButton.visibility = View.VISIBLE

                    Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getImagePath()}${AppPrefs.getSchoolID()}/parent/${parentInfoData.image}")
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .into(mainImageView)

                    if (parentInfoData.name != null) fullnameTextView.text = "${parentInfoData.name}" else fullnameTextView.text = "Not Set"
                    if (parentInfoData.tel != null) telTextView.text = "Tel: ${parentInfoData.tel}" else telTextView.text = "Tel: Not Set"

                    pidTextView.text = "User ID: ${parentInfoData.parent_pid}"
                    schoolIDTextView.text = "School : ${parentInfoData.school_id}"

                    senderRecyclerView.layoutManager = LinearLayoutManager(context)
                    senderRecyclerView.adapter = SenderAdapter(either.data.senders.distinctBy { it.parent_pid }, this)

                    if (either.data.senders.size >= 3) {
                        addSenderButton.visibility = View.GONE
                    }

                    studentRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    studentRecyclerView.adapter = MyStudentAdapter(either.data.students, this)

                } else {
                    updateProfileButton.visibility = View.INVISIBLE
                    addSenderButton.visibility = View.INVISIBLE
                }
            } else {
                if (either?.error == ApiError.GET_PARENT_INFO) {

                }
                updateProfileButton.visibility = View.INVISIBLE
                addSenderButton.visibility = View.INVISIBLE
            }
        })
    }

    override fun studentItemClicked(student: NewStudentData) {
        listener?.onStudentItemClicked(student)
    }

    override fun senderItemClicked(sender: SenderData) {
        senderListener?.onSenderItemClicked(sender)
    }

}
