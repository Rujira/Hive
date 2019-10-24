package com.codinghub.apps.hive.ui.notifications.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.notifications_message.MessageData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_three_line.view.*

class MessageListAdapter (private val messages: List<MessageData>,
                          private val clickListener: MessageListRecyclerViewClickListener) :
    RecyclerView.Adapter<MessageListAdapter.MessageViewHolder>() {

    interface MessageListRecyclerViewClickListener {
        fun messageItemClicked(message: MessageData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_three_line, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/log/${messages[position].image}").into(holder.messageImageView)
        holder.messageTitleTextView.text = "Notify Message"
        holder.messageSubtitleTextView.text = "${messages[position].parent_name} have come to get ${messages[position].student_name}."
        holder.messageDateTimeTextView.text = "${messages[position].created_date}"

        holder.itemView.setOnClickListener {
            clickListener.messageItemClicked(messages[position])
        }
    }

    class MessageViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val messageImageView: ImageView = view.rowImageView
        val messageTitleTextView: TextView = view.rowTitleTextView
        val messageSubtitleTextView: TextView = view.rowSubtitleTextView
        val messageDateTimeTextView: TextView = view.rowDateTimeTextView
    }

}