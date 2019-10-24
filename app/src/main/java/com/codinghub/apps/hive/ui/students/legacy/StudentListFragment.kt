package com.codinghub.apps.hive.ui.students.legacy


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.student.StudentData
import kotlinx.android.synthetic.main.fragment_student_list.*

class StudentListFragment : Fragment(),
    StudentListAdapter.StudentListRecyclerViewClickListener {

    private val listener: OnStudentItemInteractionListener? = null

    interface OnStudentItemInteractionListener {
        fun onStudentItemClicked(studentData: StudentData)
    }

    private val TAG = StudentListFragment::class.qualifiedName

    companion object {
        fun newInstance(title: String, studentList: ArrayList<StudentData>) : StudentListFragment {

            val fragment = StudentListFragment()
            val args = Bundle()
            args.putString(StudentsFragment.INTENT_TITLE_KEY, title)
            args.putParcelableArrayList(StudentsFragment.INTENT_LIST_KEY, studentList)

            fragment.arguments = args
            return fragment
        }

        const val STUDENT_NAME_KEY = "std_name"
        const val STUDENT_DETAIL_REQUEST_CODE = 331
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

        val args = arguments
        studentListRecyclerView.layoutManager = LinearLayoutManager(context)
        studentListRecyclerView.adapter =
            StudentListAdapter(
                filterStudentByRoom(
                    args?.getString(StudentsFragment.INTENT_TITLE_KEY),
                    args?.getParcelableArrayList(StudentsFragment.INTENT_LIST_KEY)
                ), this
            )
    }

    private fun filterStudentByRoom(room: String?, students: ArrayList<StudentData>?): List<StudentData>{
        return students!!.filter { s -> s.room == room }
    }

    override fun studentItemClicked(student: StudentData) {
        listener?.onStudentItemClicked(student)

        Log.d(TAG, "Click st ${student}")

        val studentDetailIntent = Intent(activity, StudentDetailsActivity::class.java)
        studentDetailIntent.putExtra(STUDENT_NAME_KEY, student.name)
        startActivityForResult(studentDetailIntent, STUDENT_DETAIL_REQUEST_CODE)
    }
}
