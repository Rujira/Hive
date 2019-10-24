package com.codinghub.apps.hive.ui.requestpickup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.student.StudentData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.studentnew.NewStudentData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_parent_student.view.*

class PickUpRequestAdapter(private val studentData: List<NewStudentData>) : RecyclerView.Adapter<PickUpRequestAdapter.PickUpRequestViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickUpRequestViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_parent_student, parent, false)
        return PickUpRequestViewHolder(view)
    }

    override fun getItemCount(): Int = studentData.size

    override fun onBindViewHolder(holder: PickUpRequestViewHolder, position: Int) {
        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/student/${studentData[position].image}").into(holder.studentImage)
        holder.studentName.text = studentData[position].name
        holder.studentRoom.text = studentData[position].room
        holder.checkImageView.visibility = View.INVISIBLE
    }

    class PickUpRequestViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val studentImage: ImageView = view.psImageView
        val studentName: TextView = view.psStudentNameTextView
        val studentRoom: TextView = view.psRoomTextView
        val checkImageView: ImageView = view.checkImageView
    }
}