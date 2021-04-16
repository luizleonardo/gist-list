package com.example.gistlist.ui.gistList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R

class LoadingAdapter : RecyclerView.Adapter<LoadingViewHolder>() {

    var isPaging: Boolean = false
        set(value) {
            if (field != value) {
                if (field && !value) {
                    notifyItemRemoved(0)
                } else if (value && !field) {
                    notifyItemInserted(0)
                } else if (field && value) {
                    notifyItemChanged(0)
                }
                field = value
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingViewHolder {
        return LoadingViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.layout_loading,
                    parent,
                    false
                )
        )
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, position: Int) {
        holder.bind(isPaging)
    }

    override fun getItemCount() = if (isPaging) 1 else 0

}