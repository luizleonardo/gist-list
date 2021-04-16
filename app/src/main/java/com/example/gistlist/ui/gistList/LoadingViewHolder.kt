package com.example.gistlist.ui.gistList

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.ext.gone
import com.example.gistlist.ext.visible
import kotlinx.android.synthetic.main.layout_loading.view.*

class LoadingViewHolder(
    itemView: View
) :
    RecyclerView.ViewHolder(itemView) {

    private val progress = itemView.view_holder_loading_progress_bar

    fun bind(data: Boolean) {
        if (data) {
            progress.visible()
        } else {
            progress.gone()
        }
    }
}