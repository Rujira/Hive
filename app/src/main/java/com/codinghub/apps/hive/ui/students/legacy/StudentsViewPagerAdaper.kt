package com.codinghub.apps.hive.ui.students.legacy

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.codinghub.apps.hive.model.student.student.StudentData

class StudentsViewPagerAdaper(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val titleList: MutableList<String> = ArrayList()
    private val studentList: MutableList<List<StudentData>> = ArrayList()

    override fun getItem(position: Int): Fragment {
       //return fragmentList[position]
        return StudentListFragment.newInstance(
            titleList[position],
            ArrayList(studentList[position])
        )

    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String, students: List<StudentData>) {
        fragmentList.add(fragment)
        titleList.add(title)
        studentList.add(students)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}