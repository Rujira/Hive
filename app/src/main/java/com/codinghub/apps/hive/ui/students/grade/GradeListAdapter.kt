package com.codinghub.apps.hive.ui.students.grade

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.grade.GradeData
import kotlinx.android.synthetic.main.row_two_line.view.*

class GradeListAdapter(private val grades: List<GradeData>,
                   private val clickListener: GradeListRecyclerViewClickListener) : RecyclerView.Adapter<GradeListAdapter.GradeViewHolder>() {

    interface GradeListRecyclerViewClickListener {
        fun gradeItemClicked(grade: GradeData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GradeViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_one_line_card, parent, false)
        return GradeViewHolder(view)

    }
    override fun getItemCount() = grades.size

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {

        holder.titleTextView.text = grades[position].name
        holder.itemView.setOnClickListener{
            clickListener.gradeItemClicked(grades[position])
        }

    }

    class GradeViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.rowTitleTextView

    }
}