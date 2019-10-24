package com.codinghub.apps.hive.ui.myaccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_parent_student.view.*

class MyStudentAdapter (private val students: List<NewStudentData>,
                        private val clickListener: MyStudentListRecyclerViewClickListener) : RecyclerView.Adapter<MyStudentAdapter.MyStudentViewHolder>() {

    interface MyStudentListRecyclerViewClickListener {
        fun studentItemClicked(student: NewStudentData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStudentViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_parent_student, parent, false)
        return MyStudentViewHolder(view)
    }

    override fun getItemCount() = students.size

    override fun onBindViewHolder(holder: MyStudentViewHolder, position: Int) {

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/student/${students[position].image}").into(holder.studentImage)
        //  Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getStudentImagePath()}${studentData[position].picture}").into(holder.studentImage)
        holder.studentName.text = students[position].name
        holder.studentRoom.text = students[position].room
        holder.checkImageView.visibility = View.GONE
    }

    class MyStudentViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val studentImage: ImageView = view.psImageView
        val studentName: TextView = view.psStudentNameTextView
        val studentRoom: TextView = view.psRoomTextView
        val checkImageView: ImageView = view.checkImageView

    }
}