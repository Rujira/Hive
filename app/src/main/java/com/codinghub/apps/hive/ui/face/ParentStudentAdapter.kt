package com.codinghub.apps.hive.ui.face

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.identifyparent.ParentStudentData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_parent_student.view.*

class ParentStudentAdapter(private val studentData: List<ParentStudentData>,
                           private val clickListener: ParentStudentRecyclerViewClickListener) : RecyclerView.Adapter<ParentStudentAdapter.ParentStudentViewHolder>(){

    interface ParentStudentRecyclerViewClickListener {
        fun parentStudentItemClicked(studentData: ParentStudentData, state: Int)
    }

    init {
        setHasStableIds(true)
    }

    private var tracker: SelectionTracker<Long>? = null

    fun setTracker(tracker: SelectionTracker<Long>?) {
        this.tracker = tracker
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentStudentViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_parent_student, parent, false)
        return ParentStudentViewHolder(view)

    }

    override fun getItemCount() = studentData.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onBindViewHolder(holder: ParentStudentViewHolder, position: Int) {

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/student/${studentData[position].image}").into(holder.studentImage)
      //  Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getStudentImagePath()}${studentData[position].picture}").into(holder.studentImage)
        holder.studentName.text = studentData[position].name
        holder.studentRoom.text = studentData[position].room

        if (tracker!!.isSelected(position.toLong())) {
            holder.checkImageView.visibility = View.VISIBLE
            clickListener.parentStudentItemClicked(studentData[position], 1)
        } else {
            holder.checkImageView.visibility = View.INVISIBLE
            clickListener.parentStudentItemClicked(studentData[position], 0)
        }

    }

    class ParentStudentViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val studentImage: ImageView = view.psImageView
        val studentName: TextView = view.psStudentNameTextView
        val studentRoom: TextView = view.psRoomTextView
        val checkImageView: ImageView = view.checkImageView

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): Long? = itemId

        }
    }

    class MyLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                return (recyclerView.getChildViewHolder(view) as ParentStudentViewHolder).getItemDetails()
            }
            return null
        }
    }


}

