package com.example.gistlist.ui.gistList.footer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.ui.helper.gone
import com.example.gistlist.ui.helper.visible
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