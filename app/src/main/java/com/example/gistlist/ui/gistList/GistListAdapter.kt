package com.example.gistlist.ui.gistList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem

class GistListAdapter(
    private val gistItemCallback: GistListViewHolder.GistItemCallback
) :
    ListAdapter<GistItem, GistListViewHolder>(
        object :
            DiffUtil.ItemCallback<GistItem>() {
            override fun areItemsTheSame(oldItem: GistItem, newItem: GistItem) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GistItem, newItem: GistItem) =
                oldItem == newItem
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GistListViewHolder {
        return GistListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_gist, parent, false),
            gistItemCallback
        )
    }

    override fun onBindViewHolder(holder: GistListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

}