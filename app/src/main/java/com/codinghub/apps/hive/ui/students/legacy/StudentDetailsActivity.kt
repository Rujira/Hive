package com.codinghub.apps.hive.ui.students.legacy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.codinghub.apps.hive.model.utilities.SafeClickListener
import com.codinghub.apps.hive.ui.requestpickup.PickUpRequestActivity
import com.codinghub.apps.hive.ui.students.student.StudentListActivity
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_student_details.*

class StudentDetailsActivity : AppCompatActivity() {

    private lateinit var studentsViewModel: StudentsViewModel

    lateinit var studentData: NewStudentData

    private val TAG = StudentDetailsActivity::class.qualifiedName

    companion object {
        const val INTENT_PICK_UP_REQUEST_KEY = "pickuprequest"
        const val INTENT_PICK_UP_REQUEST_CODE = 125
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        this.title = getString(R.string.student_details_title)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        // studentData = intent.getParcelableExtra(StudentListFragment.STUDENT_DETAIL_LIST_KEY)
        studentData = intent.getParcelableExtra(StudentListActivity.STUDENT_LIST_KEY)

        Log.d(TAG, "Student ${intent.getStringExtra(StudentListFragment.STUDENT_NAME_KEY)}")
        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getImagePath()}${AppPrefs.getSchoolID()}/student/${studentData.image}").into(studentDetailImageView)
      //  Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getStudentImagePath()}${studentData.image}").into(studentDetailImageView)
        studentDetailFullNameTextView.text = studentData.name
        studentDetailIDRoomTextView.text = "ID: ${studentData.student_pid} Room: ${studentData.room}"

        teacherNameTextView.setText(studentData.teacher_pid)
        teacherNameTextView.isFocusable = false

        parentNameTextView.setText(studentData.parent_pid)
        parentNameTextView.isFocusable = false

        makeRequestButton.setSafeOnClickListener {

            val requestPickUpIntent = Intent(this@StudentDetailsActivity, PickUpRequestActivity::class.java)
            requestPickUpIntent.putExtra(INTENT_PICK_UP_REQUEST_KEY, studentData)
            startActivityForResult(requestPickUpIntent,
                INTENT_PICK_UP_REQUEST_CODE
            )
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



}
