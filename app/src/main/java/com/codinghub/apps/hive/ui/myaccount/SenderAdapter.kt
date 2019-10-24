package com.codinghub.apps.hive.ui.myaccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.myaccount.parent.SenderData
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_two_line.view.*

class SenderAdapter(private val senders: List<SenderData>,
                    private val clickListener: SenderListRecyclerViewClickListener) : RecyclerView.Adapter<SenderAdapter.SenderViewHolder>() {


    interface SenderListRecyclerViewClickListener {
        fun senderItemClicked(sender: SenderData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SenderViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row_two_line, parent, false)
        return SenderViewHolder(view)

    }

    override fun getItemCount() = senders.size

    override fun onBindViewHolder(holder: SenderViewHolder, position: Int) {

        Picasso.get().load("${AppPrefs.getServiceURL()}${AppPrefs.getThumbnailPath()}${AppPrefs.getSchoolID()}/parent/${senders[position].image}")
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE).into(holder.senderImage)
        holder.titleTextView.text = senders[position].name
        holder.subtitleTextView.text = senders[position].tel

        if (senders[position] == senders.last()) {
            holder.rowListDivider.visibility = View.GONE
        }

    }

    class SenderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val senderImage: ImageView = view.rowImageView
        val titleTextView: TextView = view.rowTitleTextView
        val subtitleTextView: TextView = view.rowSubtitleTextView
        val rowListDivider: View = view.rowListDivider
    }
}