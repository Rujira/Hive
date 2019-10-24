package com.codinghub.apps.hive.ui.facerecognition

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.identifyparent.ParentStudentData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_two_line.view.*

class FaceRecognitionStudentAdapter (private val students: List<ParentStudentData>) : RecyclerView.Adapter<FaceRecognitionStudentAdapter.FaceRecognitionStudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceRecognitionStudentViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_two_line, parent, false)
        return FaceRecognitionStudentViewHolder(view)
    }

    override fun getItemCount() = students.size

    override fun onBindViewHolder(holder: FaceRecognitionStudentViewHolder, position: Int) {

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/student/${students[position].image}").into(holder.studentImage)
        //  Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getStudentImagePath()}${studentData[position].picture}").into(holder.studentImage)
        holder.titleTextView.text = students[position].name
        holder.subtitleTextView.text = students[position].student_pid

        if (students[position] == students.last()) {
            holder.rowListDivider.visibility = View.GONE
        }

    }

    class FaceRecognitionStudentViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val studentImage: ImageView = view.rowImageView
        val titleTextView: TextView = view.rowTitleTextView
        val subtitleTextView: TextView = view.rowSubtitleTextView
        val rowListDivider: View = view.rowListDivider

    }
}