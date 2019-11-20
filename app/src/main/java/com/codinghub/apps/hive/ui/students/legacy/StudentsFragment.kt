package com.codinghub.apps.hive.ui.students.legacy

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager

import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.student.student.StudentResponse
import com.codinghub.apps.hive.model.error.ApiError
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.error.Status
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.model.myaccount.teacher.TeacherUserInfoResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.grade.GradeData
import com.codinghub.apps.hive.model.student.grade.GradeResponse
import com.codinghub.apps.hive.model.student.room.RoomData
import com.codinghub.apps.hive.model.student.room.RoomResponse
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse
import com.codinghub.apps.hive.ui.main.MainActivity
import com.codinghub.apps.hive.ui.students.room.RoomListAdapter
import com.codinghub.apps.hive.ui.students.student.NewStudentListAdapter
import com.codinghub.apps.hive.viewmodel.StudentsViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_room.*
import kotlinx.android.synthetic.main.activity_student_list.*

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

    private var roomList: MutableList<RoomData> = ArrayList()
    private var didFinishingIntialView: Boolean = false

    private lateinit var pagerAdapter: StudentsViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.title = getString(R.string.students_title)

        Log.d(TAG, "Student Fragment On Create View")

        val view = inflater.inflate(R.layout.fragment_students, container, false)

        studentsViewModel = ViewModelProviders.of(this).get(StudentsViewModel::class.java)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.setGroupVisible(R.id.main_menu_group, true)
        super.onPrepareOptionsMenu(menu)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        when(studentsViewModel.getCurrentUser().first().role) {
            UserRole.ADMIN -> {

                inflater.inflate(R.menu.action_bar_spinner_menu, menu)
                val item = menu.findItem(R.id.menu_spinner)
                val spinner = item.actionView as Spinner

                studentsViewModel.listGrade(AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<GradeResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {

                            val grades: List<GradeData> = either.data.data
                            val spinnerTitle = arrayListOf<String>()

                            for (gradeName in grades) {
                                spinnerTitle.add(gradeName.name)
                            }

                            spinner.adapter = ArrayAdapter<String>(activity!!.applicationContext, R.layout.spinner_item, spinnerTitle)
                            spinner.setSelection(AppPrefs.getSelectedGrade().first().id -1)
                            spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    Log.d(TAG, "On Nothing Selected")
                                }
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    Log.d(TAG, "On Item Selected ${grades[position]}")

                                    studentsViewModel.saveSelectedGrade(grades[position])

                                    reloadFragment()

                                }
                            }

                        } else {

                            Toast.makeText(activity?.applicationContext, "Student List is Empty", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.LIST_GRADE) {
                            Toast.makeText(activity?.applicationContext, R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    }
                })

            }

            UserRole.TEACHER -> {

                inflater.inflate(R.menu.action_bar_spinner_menu, menu)
                val item = menu.findItem(R.id.menu_spinner)
                val spinner = item.actionView as Spinner

                studentsViewModel.listGrade(AppPrefs.getSchoolID().toString()).observe(this, Observer<Either<GradeResponse>> { either ->
                    if (either?.status == Status.SUCCESS && either.data != null) {
                        if (either.data.ret == 0 && either.data.data.isNotEmpty()) {

                            val grades: List<GradeData> = either.data.data
                            val spinnerTitle = arrayListOf<String>()

                            for (gradeName in grades) {
                                spinnerTitle.add(gradeName.name)
                            }

                            spinner.adapter = ArrayAdapter<String>(activity!!.applicationContext, R.layout.spinner_item, spinnerTitle)
                            spinner.setSelection(AppPrefs.getSelectedGrade().first().id -1)
                            spinner.onItemSelectedListener = object  : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    Log.d(TAG, "On Nothing Selected")
                                }
                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    Log.d(TAG, "On Item Selected ${grades[position]}")

                                    studentsViewModel.saveSelectedGrade(grades[position])

                                    reloadFragment()

                                }
                            }

                        } else {

                            Toast.makeText(activity?.applicationContext, "Student List is Empty", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (either?.error == ApiError.LIST_GRADE) {
                            Toast.makeText(activity?.applicationContext, R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                        }
                    }
                })

            }

            UserRole.PARENT -> {

            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "Student Fragment On Activity Created")

        view?.let {
            studentsViewPager = it.findViewById(R.id.studentsViewPager)
            studentTabs = it.findViewById(R.id.studentTabs)
            emptyStudentLayout = it.findViewById(R.id.emptyStudentLayout)
            emptyStudentLayout.visibility = View.GONE

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "Student Fragment On View Created")
       // reloadFragment()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "On Destroy view")

    }



    private fun reloadFragment() {

        roomList.clear()
        listRoomByGrade(AppPrefs.getSelectedGrade().first().grade)

    }

    private fun listRoomByGrade(grade: String) {

        Log.d(TAG, "List Room By Grade")
        studentsViewModel.listRoom(AppPrefs.getSchoolID().toString(), grade).observe(this, Observer<Either<RoomResponse>> { either ->
            if (either?.status == Status.SUCCESS && either.data != null) {
                if (either.data.ret == 0 && either.data.data.isNotEmpty()) {

                    for (room in either.data.data) {
                        roomList.add(room)
                        //listStudentByRoom(selectedGrade, room.room)
                    }

                    configureRoomViewPager(roomList)

                } else {

                    emptyStudentLayout.visibility = View.VISIBLE
                    Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
                }
            } else {
                if (either?.error == ApiError.LIST_ROOM) {
                    //  Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()

                }

                emptyStudentLayout.visibility = View.VISIBLE
                Toast.makeText(context,R.string.error_retrieving_student, Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun configureRoomViewPager(roomList: List<RoomData>) {

        //val roomTitles = distinctRoomTitleBy(students)

        Log.d(TAG, "Configure Room")

        pagerAdapter = StudentsViewPagerAdapter(childFragmentManager, roomList)
        studentsViewPager.adapter = pagerAdapter
        studentTabs.setupWithViewPager(studentsViewPager)
        pagerAdapter.notifyDataSetChanged()

//        for (room in roomList) {
//         //   Log.d("StudentTest", "Room : ${room}")
//            studentsAdapter.addFragment(StudentListFragment(), room.name, room)
//        }
//
//        studentsViewPager.adapter = studentsAdapter
//        studentTabs.setupWithViewPager(studentsViewPager)

    }


}
