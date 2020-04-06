package com.sanislo.bvtech.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sanislo.bvtech.R

class FeedListAdapter :
    ListAdapter<FeedListItem, FeedListAdapter.FeedViewHolder>(asyncDifferConfig) {

    companion object {
        val asyncDifferConfig = AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<FeedListItem>() {
            override fun areItemsTheSame(oldListItem: FeedListItem, newListItem: FeedListItem): Boolean {
                return oldListItem.id == newListItem.id
            }

            override fun areContentsTheSame(oldListItem: FeedListItem, newListItem: FeedListItem): Boolean {
                return oldListItem == newListItem
            }
        }).build()
    }

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUserName = itemView.findViewById<TextView>(R.id.tv_name)
        private val tvText = itemView.findViewById<TextView>(R.id.tv_text)

        fun bind() {
            getItem(adapterPosition).run {
                tvUserName.text = userName
                tvText.text = text
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_feed, parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind()
    }
}