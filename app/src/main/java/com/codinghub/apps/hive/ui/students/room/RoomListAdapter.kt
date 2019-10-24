package com.codinghub.apps.hive.ui.students.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.student.room.RoomData
import kotlinx.android.synthetic.main.row_one_line_card.view.*

class RoomListAdapter(private val rooms: List<RoomData>,
                       private val clickListener: RoomListRecyclerViewClickListener) : RecyclerView.Adapter<RoomListAdapter.RoomViewHolder>() {

    interface RoomListRecyclerViewClickListener {
        fun roomItemClicked(room: RoomData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_one_line_card, parent, false)
        return RoomViewHolder(view)

    }
    override fun getItemCount() = rooms.size

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {

        holder.titleTextView.text = rooms[position].name
        holder.itemView.setOnClickListener{
            clickListener.roomItemClicked(rooms[position])
        }
    }

    class RoomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.rowTitleTextView


    }
}