package com.codinghub.apps.hive.ui.students.legacy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.student.student.StudentResponse
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import com.google.android.material.tabs.TabLayout

class StudentsFragment : Fragment() {

    companion object {
        const val INTENT_LIST_KEY = "studentlist"
        const val INTENT_TITLE_KEY = "roomtitle"
    }

    private lateinit var studentsViewModel: StudentsViewModel
    private val TAG = StudentsFragment::class.qualifiedName

    lateinit var studentsViewPager: ViewPager
    lateinit var studentTabs: TabLayout
    lateinit var emptyStudentLayout: FrameLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.students_title)

        val view = inflater.inflate(R.layout.fragment_students, container, false)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.let {
            studentsViewPager = it.findViewById(R.id.studentsViewPager)
            studentTabs = it.findViewById(R.id.studentTabs)
            emptyStudentLayout = it.findViewById(R.id.emptyStudentLayout)
            emptyStudentLayout.visibility = View.GONE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        studentsViewModel.getStudents().observe(this, Observer<Either<StudentResponse>> { either ->
//            if (either?.status == Status.SUCCESS && either.data != null) {
//                if (either.data.ret == 0) {
//                    Log.d(TAG, "StudentData : ${either.data.students}")
//                    configureRoomViewPager(either.data.students)
//
//                } else {
//                    emptyStudentLayout.visibility = View.VISIBLE
//                    Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                if (either?.error == ApiError.STUDENTS) {
//                    emptyStudentLayout.visibility = View.VISIBLE
//                    Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
//                }
//            }
//        })
    }


    private fun configureRoomViewPager(students: List<StudentData>) {

        val roomTitles = distinctRoomTitleBy(students)
        val studentsAdaper =
            StudentsViewPagerAdaper(childFragmentManager)

        for (title in roomTitles) {
            studentsAdaper.addFragment(StudentListFragment(), title, students)
        }

        studentsViewPager.adapter = studentsAdaper
        studentTabs.setupWithViewPager(studentsViewPager)
    }

    private fun distinctRoomTitleBy(students: List<StudentData>) : List<String> {

        var rooms = arrayListOf<String>()
        for (student in students) {
            student.room?.let { rooms.add(it) }
        }
        return rooms.distinct()

    }

}
