package com.codinghub.apps.hive.ui.students.legacy

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.codinghub.apps.hive.model.student.room.RoomData
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData

class StudentsViewPagerAdapter(fragmentManager: FragmentManager, private val rooms: List<RoomData>) :
    FragmentStatePagerAdapter(fragmentManager) {

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getItem(position: Int): Fragment {
        return StudentListFragment.newInstance(rooms[position])
    }

    override fun getCount(): Int {
        return rooms.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return rooms[position].name
    }
}