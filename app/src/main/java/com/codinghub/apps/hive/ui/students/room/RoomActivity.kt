package com.codinghub.apps.hive.ui.students.room

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.room.RoomData
import com.codinghub.apps.hive.model.student.room.RoomResponse
import com.codinghub.apps.hive.ui.face.FaceFragment
import com.codinghub.apps.hive.ui.students.grade.GradeFragment
import com.codinghub.apps.hive.ui.students.student.StudentListActivity
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import kotlinx.android.synthetic.main.activity_room.*

class RoomActivity : AppCompatActivity(), RoomListAdapter.RoomListRecyclerViewClickListener {

    private val listener: OnRoomItemInteractionListener? = null

    interface OnRoomItemInteractionListener {
        fun onRoomItemClicked(room: RoomData)
    }

    private lateinit var studentsViewModel: StudentsViewModel

    private val TAG = RoomActivity::class.qualifiedName

    companion object {
        const val ROOM_LIST_KEY = "roomtostudentkey"
        const val ROOM_REQUEST_CODE = 112
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        title = "Room"

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        assert(supportActionBar != null)   //null check
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f

        Log.d(TAG,"Grade ${intent.getStringExtra(GradeFragment.GRADE_LIST_KEY)}")

        loadRoomData(AppPrefs.getSchoolID().toString(), intent.getStringExtra(GradeFragment.GRADE_LIST_KEY))
    }

    private fun loadRoomData(schoolID: String, grade: String) {
        studentsViewModel.listRoom(schoolID, grade).observe(this, Observer<Either<RoomResponse>> { either ->
            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                   // emptyStudentLayout.visibility = View.INVISIBLE

                    val rooms: List<RoomData> = either.data.data
                    roomRecyclerView.layoutManager = LinearLayoutManager(this)
                    roomRecyclerView.adapter = RoomListAdapter(rooms, this)

                } else {

                    //emptyStudentLayout.visibility = View.VISIBLE
                    // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                }
            } else {
                if (either?.error == ApiError.LIST_ROOM) {
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

    override fun roomItemClicked(room: RoomData) {
        listener?.onRoomItemClicked(room)

        val pickUpIntent = Intent(this, StudentListActivity::class.java)
        pickUpIntent.putExtra(ROOM_LIST_KEY, room)
        startActivityForResult(pickUpIntent, ROOM_REQUEST_CODE)

    }
}
