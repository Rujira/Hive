package com.codinghub.apps.hive.ui.students.legacy


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
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.room.RoomData
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse
import com.codinghub.apps.hive.ui.students.student.NewStudentListAdapter
import com.codinghub.apps.hive.ui.students.student.StudentListActivity
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import kotlinx.android.synthetic.main.activity_student_list.*
import kotlinx.android.synthetic.main.fragment_student_list.*
import kotlinx.android.synthetic.main.fragment_student_list.studentListRecyclerView

class StudentListFragment : Fragment(), NewStudentListAdapter.StudentListRecyclerViewClickListener {

    private val listener: OnNewStudentItemInteractionListener? = null

    interface OnNewStudentItemInteractionListener {
        fun onStudentItemClicked(student: NewStudentData)
    }

    private val TAG = StudentListFragment::class.qualifiedName

    private lateinit var studentsViewModel: StudentsViewModel

    companion object {
        fun newInstance(roomData: RoomData) : StudentListFragment {

            val fragment = StudentListFragment()
            val args = Bundle()

           // args.putString(StudentsFragment.INTENT_TITLE_KEY, title)
            args.putParcelable(StudentsFragment.INTENT_LIST_KEY, roomData)
           // args.putParcelableArrayList(StudentsFragment.INTENT_LIST_KEY, roomData)
            Log.d("StudentTest","New Instance : ${args}")

            fragment.arguments = args
            return fragment
        }

        const val STUDENT_LIST_KEY = "studentlistkey"
        const val STUDENT_LIST_REQUEST_CODE = 113
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_student_list, container, false)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        val args = arguments

       // val roomData: RoomData = args?.getParcelableArrayList<RoomData>(StudentsFragment.INTENT_LIST_KEY)
        Log.d("StudentTest", "Room Title ${args?.getString(StudentsFragment.INTENT_TITLE_KEY)}")
        Log.d("StudentTest", "Room Data ${args?.getParcelable<RoomData>(StudentsFragment.INTENT_LIST_KEY)}")

        var roomData = args?.getParcelable<RoomData>(StudentsFragment.INTENT_LIST_KEY)

       listStudentByRoom(roomData!!.grade, roomData.room)

    }

    private fun listStudentByRoom(grade: String, room: String) {

        Log.d(TAG, "List Student By Room")

        studentsViewModel.listStudent(AppPrefs.getSchoolID().toString(), grade, room)
            .observe(this, Observer<Either<NewStudentResponse>> { either ->
                if (either?.status == Status.SUCCESS && either.data != null) {
                    if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                        // emptyStudentLayout.visibility = View.INVISIBLE

                        val students: List<NewStudentData> = either.data.data
                        //   Log.d(TAG, "Student ${either.data.data}")
                        studentListRecyclerView.layoutManager = LinearLayoutManager(context)
                        studentListRecyclerView.adapter = NewStudentListAdapter(students, this)

                        Log.d(TAG, "studentList ${students}")


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
        //
    }

    override fun studentItemClicked(student: NewStudentData) {

        listener?.onStudentItemClicked(student)

        val studentDetailsIntent = Intent(context, StudentDetailsActivity::class.java)
        studentDetailsIntent.putExtra(STUDENT_LIST_KEY, student)
        startActivityForResult(studentDetailsIntent, STUDENT_LIST_REQUEST_CODE)
    }

    //
//    private fun filterStudentByRoom(room: String?, students: ArrayList<NewStudentData>?): List<NewStudentData>{
//       // Log.d(TAG, "Student : ${students}")
//
//        return students!!.filter { s -> s.room == room }
//    }

//    override fun studentItemClicked(student: NewStudentData) {
//        listener?.onStudentItemClicked(student)
//
//        Log.d(TAG, "Click st ${student}")
//
//        val studentDetailIntent = Intent(activity, StudentDetailsActivity::class.java)
//        studentDetailIntent.putExtra(STUDENT_NAME_KEY, student.name)
//        startActivityForResult(studentDetailIntent, STUDENT_DETAIL_REQUEST_CODE)
//    }
}
