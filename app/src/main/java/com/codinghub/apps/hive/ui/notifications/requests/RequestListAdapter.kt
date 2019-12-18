package com.codinghub.apps.hive.ui.notifications.requests

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.notification_request.RequestData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_three_line.view.*
import kotlinx.android.synthetic.main.row_three_line.view.rowDateTimeTextView
import kotlinx.android.synthetic.main.row_three_line.view.rowImageView
import kotlinx.android.synthetic.main.row_three_line.view.rowSubtitleTextView
import kotlinx.android.synthetic.main.row_three_line.view.rowTitleTextView
import kotlinx.android.synthetic.main.row_three_line_status.view.*

class RequestListAdapter (private val requests: MutableList<RequestData>,
                          private val clickListener: RequestListRecyclerViewClickListener) :
    RecyclerView.Adapter<RequestListAdapter.RequestViewHolder>() {

    interface RequestListRecyclerViewClickListener {
        fun requestItemClicked(request: RequestData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_three_line_status, parent, false)
        return RequestViewHolder(view)
    }

    override fun getItemCount() = requests.size

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/log/${requests[position].image}").into(holder.requestImageView)

        holder.requestTitleTextView.text = "Request Message"
        holder.requestSubtitleTextView.text = "This person've come to get ${requests[position].student_name}. Please approve to allow for pickup or decline."
        holder.requestDateTimeTextView.text = "${requests[position].created_date}"

        when(requests[position].status) {
            "ALLOW" -> {
                holder.statusChip.setChipBackgroundColorResource(R.color.approveColor)
                holder.statusChip.text = "Allow"
                holder.statusChip.setChipIconResource(R.drawable.ic_approve)
            }
            "NOTALLOW" -> {
                holder.statusChip.setChipBackgroundColorResource(R.color.declineColor)
                holder.statusChip.text = "Not Allow"
                holder.statusChip.setChipIconResource(R.drawable.ic_decline)
            }
            else -> {
                holder.statusChip.setChipBackgroundColorResource(R.color.waitingColor)
                holder.statusChip.text = "Waiting"
                holder.statusChip.setChipIconResource(R.drawable.ic_waiting)
            }
        }

        holder.itemView.setOnClickListener {
            clickListener.requestItemClicked(requests[position])
        }
    }

    class RequestViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val requestImageView: ImageView = view.rowImageView
        val requestTitleTextView: TextView = view.rowTitleTextView
        val requestSubtitleTextView: TextView = view.rowSubtitleTextView
        val requestDateTimeTextView: TextView = view.rowDateTimeTextView
        val statusChip: Chip = view.statusChip

    }
}