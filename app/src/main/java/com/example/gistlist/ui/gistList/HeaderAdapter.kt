package com.example.gistlist.ui.gistList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R

class HeaderAdapter(
    private val searchViewCallback: HeaderViewHolder.SearchViewCallback?
) :
    RecyclerView.Adapter<HeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        return HeaderViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(
                    R.layout.layout_search_view,
                    parent,
                    false
                ),
            searchViewCallback
        )
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 1

}