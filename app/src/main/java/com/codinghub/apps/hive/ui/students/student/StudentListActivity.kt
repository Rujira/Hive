package com.codinghub.apps.hive.ui.students.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.room.RoomData
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse
import com.codinghub.apps.hive.ui.students.legacy.StudentDetailsActivity
import com.codinghub.apps.hive.ui.students.legacy.StudentListAdapter

import com.codinghub.apps.hive.ui.students.room.RoomActivity
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import kotlinx.android.synthetic.main.activity_student_list.*

class StudentListActivity : AppCompatActivity(), NewStudentListAdapter.StudentListRecyclerViewClickListener {

    private val listener: OnNewStudentItemInteractionListener? = null

    interface OnNewStudentItemInteractionListener {
        fun onStudentItemClicked(student: NewStudentData)
    }

    private lateinit var studentsViewModel: StudentsViewModel

    private val TAG = RoomActivity::class.qualifiedName

    companion object {
        const val STUDENT_LIST_KEY = "studentlistkey"
        const val STUDENT_LIST_REQUEST_CODE = 113
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        title = "Student"

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        Log.d(TAG, "ROOM ${intent.getParcelableExtra<RoomData>(RoomActivity.ROOM_LIST_KEY)}")

        val roomData = intent.getParcelableExtra<RoomData>(RoomActivity.ROOM_LIST_KEY)

        loadStudentData(AppPrefs.getSchoolID().toString(), roomData.grade, roomData.room)
    }

    private fun loadStudentData(schoolID: String, grade: String, room: String) {
        studentsViewModel.listStudent(schoolID, grade, room)
            .observe(this, Observer<Either<NewStudentResponse>> { either ->
                if (either?.status == Status.SUCCESS && either.data != null) {
                    if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                        // emptyStudentLayout.visibility = View.INVISIBLE

                        val students: List<NewStudentData> = either.data.data
                        studentListRecyclerView.layoutManager = LinearLayoutManager(this)
                        studentListRecyclerView.adapter = NewStudentListAdapter(students, this)

                    } else {

                        //emptyStudentLayout.visibility = View.VISIBLE
                        // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (either?.error == ApiError.LIST_STUDENT) {
                        //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                    }
                    // emptyStudentLayout.visibility = View.VISIBLE
                }
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun studentItemClicked(student: NewStudentData) {
        listener?.onStudentItemClicked(student)

        val studentDetailsIntent = Intent(this, StudentDetailsActivity::class.java)
        studentDetailsIntent.putExtra(STUDENT_LIST_KEY, student)
        startActivityForResult(studentDetailsIntent, STUDENT_LIST_REQUEST_CODE)

    }
}

