package com.codinghub.apps.hive.ui.students.grade


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.grade.GradeData
import com.codinghub.apps.hive.model.student.grade.GradeResponse
import com.codinghub.apps.hive.ui.students.room.RoomActivity
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import kotlinx.android.synthetic.main.fragment_grade.*

class GradeFragment : Fragment(), GradeListAdapter.GradeListRecyclerViewClickListener {

    private val listener: OnGradeItemInteractionListener? = null

    interface OnGradeItemInteractionListener{
        fun onGradeItemClicked(grade: GradeData)
    }

    private lateinit var studentsViewModel: StudentsViewModel

    private lateinit var emptyStudentLayout: ConstraintLayout

    private val TAG = GradeFragment::class.qualifiedName

    companion object {
        const val GRADE_LIST_KEY = "gradetoroomkey"
        const val GRADE_REQUEST_CODE = 111
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = "Student"

        val view = inflater.inflate(R.layout.fragment_grade, container, false)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.let {
            emptyStudentLayout = it.findViewById(R.id.emptyStudentLayout)
            emptyStudentLayout.visibility = View.INVISIBLE
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadGradeData(AppPrefs.getSchoolID().toString())
    }

    private fun loadGradeData(schoolID: String) {

        studentsViewModel.listGrade(schoolID).observe(this, Observer<Either<GradeResponse>> { either ->
            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0 && either.data.data.isNotEmpty()) {
                    emptyStudentLayout.visibility = View.INVISIBLE


                    val grades: List<GradeData> = either.data.data
                    gradeRecyclerView.layoutManager = LinearLayoutManager(context)
                    gradeRecyclerView.adapter = GradeListAdapter(grades, this)

                } else {

                    emptyStudentLayout.visibility = View.VISIBLE
                    // Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                }
            } else {
                if (either?.error == ApiError.LIST_GRADE) {
                    //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                }
                emptyStudentLayout.visibility = View.VISIBLE
            }
        })
    }

    override fun gradeItemClicked(grade: GradeData) {

        listener?.onGradeItemClicked(grade)

        val pickUpIntent = Intent(activity, RoomActivity::class.java)
        pickUpIntent.putExtra(GRADE_LIST_KEY, grade.grade)
        startActivityForResult(pickUpIntent, GRADE_REQUEST_CODE)
    }
}
