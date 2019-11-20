package com.codinghub.apps.hive.ui.students.legacy

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
import kotlinx.android.synthetic.main.row_two_line.view.*

class StudentListAdapter(private val students: List<NewStudentData>,
                         private val clickListener: StudentListRecyclerViewClickListener
) : RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {

    interface StudentListRecyclerViewClickListener {
        fun studentItemClicked(student: NewStudentData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_two_line, parent, false)
        return StudentViewHolder(view)

    }

    override fun getItemCount() = students.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {

       // Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getStudentImagePath()}${students[position].image}").into(holder.studentImage)
        holder.titleTextView.text = students[position].name
        holder.subtitleTextView.text = students[position].student_pid
        holder.itemView.setOnClickListener {
            clickListener.studentItemClicked(students[position])
        }
    }

    class StudentViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val studentImage: ImageView = view.rowImageView
        val titleTextView: TextView = view.rowTitleTextView
        val subtitleTextView: TextView = view.rowSubtitleTextView
    }
}