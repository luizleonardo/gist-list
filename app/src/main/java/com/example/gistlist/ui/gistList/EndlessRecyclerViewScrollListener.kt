package com.example.gistlist.ui.gistList

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager


abstract class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {

    companion object {
        const val PAGE_START = 1
    }

    private var mLayoutManager: RecyclerView.LayoutManager

    private var visibleThreshold = 3
    private var hasNextPage = false
    private var nextPage = PAGE_START
    private var isPaging: Boolean = false

    constructor(layoutManager: LinearLayoutManager) {
        mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }

    private fun startPaging() {
        isPaging = true
    }

    fun stopPaging() {
        isPaging = false
    }

    fun nextPage() = nextPage

    fun hasNextPage() = hasNextPage

    private fun isPaging() = isPaging

    fun nextPage(nextPage: Int?) = apply {
        this.nextPage = nextPage ?: 1
    }

    fun hasNextPage(hasNextPage: Boolean?) = apply {
        this.hasNextPage = hasNextPage ?: false
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        when (mLayoutManager) {
            is LinearLayoutManager -> mLayoutManager as? LinearLayoutManager
            is GridLayoutManager -> mLayoutManager as? GridLayoutManager
            else -> null
        }?.let {
            val totalItemCount = it.itemCount
            val lastVisibleItem =
                if (it is GridLayoutManager) it.findLastCompletelyVisibleItemPosition()
                else it.findLastVisibleItemPosition()

            if (lastVisibleItem >= totalItemCount - visibleThreshold
                && !isPaging()
            ) {
                startPaging()
                onLoadMore(nextPage)
            }
        }
    }

    abstract fun onLoadMore(page: Int)
}