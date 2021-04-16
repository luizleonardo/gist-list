package com.example.gistlist.ui.gistList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ui.base.BaseFragment
import com.example.gistlist.ui.detail.DetailActivity
import com.example.gistlist.ui.detail.DetailActivity.Companion.EXTRA_GIST_ITEM
import com.example.gistlist.ui.detail.DetailActivity.Companion.EXTRA_GIST_ITEM_VIEW
import com.example.gistlist.ui.favorites.FavoriteViewModel
import com.example.gistlist.ui.gistList.EndlessRecyclerViewScrollListener.Companion.PAGE_START
import com.example.gistlist.ui.helper.CustomViewError
import com.example.gistlist.ui.helper.CustomViewError.Type
import com.example.gistlist.ui.helper.ViewData.Status.*
import com.example.gistlist.ui.helper.goneViews
import com.example.gistlist.ui.helper.startShowAnimation
import com.example.gistlist.ui.helper.visible
import com.example.gistlist.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_gist_list.*
import kotlinx.android.synthetic.main.fragment_gist_list.view.*
import kotlinx.android.synthetic.main.layout_search_view.*
import kotlinx.android.synthetic.main.layout_search_view.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class GistListFragment : BaseFragment(),
    GistListViewHolder.GistItemCallback,
    HeaderViewHolder.SearchViewCallback,
    CustomViewError.Callback {

    companion object {
        const val DETAIL_REQUEST_CODE = 1

        @JvmStatic
        fun newInstance(): GistListFragment = GistListFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_gist_list

    private val gistListViewModel: GistListViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var endlessRecyclerViewScrollListener: EndlessRecyclerViewScrollListener

    private val gistListAdapter = GistListAdapter(this@GistListFragment)
    private val headerAdapter = HeaderAdapter(this@GistListFragment)
    private var loadingAdapter = LoadingAdapter()

    private var lastSearch: String? = ""
    private var nextPage: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(gistListViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observePublicGistList(this)
            observePublicGistListPagination(this)
            observeSearch(this)
            observeSearchPagination(this)
        }

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavorite(this)
        }

        return view
    }

    override fun setupView(view: View) {
        postponeEnterTransition()
        super.setupView(view)
        setupSnackBar(view)
        setupRecyclerView(view)
        fragment_gist_list_error.click(this@GistListFragment)
        gistListViewModel.fetchPublicGists()
    }

    private fun setupRecyclerView(view: View) {
        view.fragment_gist_list_recycler_view.apply {
            setHasFixedSize(true)
            linearLayoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.VERTICAL, false
            )
            endlessRecyclerViewScrollListener =
                object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
                    override fun onLoadMore(page: Int) {
                        nextPage = page + 1
                        if (!lastSearch.isNullOrEmpty()) {
                            gistListViewModel.searchByUsernamePagination(
                                username = lastSearch,
                                page = page
                            )
                            return
                        }
                        gistListViewModel.fetchPublicGistsPagination(page = page)
                    }
                }
            layoutManager = linearLayoutManager
            adapter = ConcatAdapter(headerAdapter, gistListAdapter, loadingAdapter)
            addOnScrollListener(endlessRecyclerViewScrollListener)
        }
    }

    private fun setupSnackBar(view: View) {
        snackBar = Snackbar.make(
            view.fragment_gist_list_content_holder,
            "",
            Snackbar.LENGTH_SHORT
        )
    }

    private fun showSnackBar(message: String?) {
        snackBar?.run {
            if (!isShown) this.show() else this.dismiss()
            snackBarTextView()?.text = message
        }
    }

    private fun observePublicGistList(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataGists.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    LOADING -> {
                        goneViews(fragment_gist_list_recycler_view, fragment_gist_list_error)
                        fragment_gist_list_progress_bar.visible()
                    }
                    COMPLETE -> {
                        goneViews(fragment_gist_list_progress_bar, fragment_gist_list_error)
                        fragment_gist_list_recycler_view.visible()
                        fragment_gist_list_recycler_view.startShowAnimation()
                        if (!it.data.isNullOrEmpty()) {
                            gistListAdapter.submitList(it.data)
                        } else {
                            //setupEmptyView()
                        }
                    }
                    ERROR -> {
                        goneViews(fragment_gist_list_progress_bar, fragment_gist_list_recycler_view)
                        fragment_gist_list_error.apply {
                            type(Type.GENERIC)
                            message(it.error?.message)
                            build()
                            visible()
                        }
                    }
                }
            }
        )
    }

    private fun observePublicGistListPagination(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataGistsPagination.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    LOADING -> {
                        loadingAdapter.isPaging = true
                    }
                    COMPLETE -> {
                        loadingAdapter.isPaging = false
                        updateRecycleViewItems(it.data)
                    }
                    ERROR -> {
                        loadingAdapter.isPaging = false
                    }
                }
            }
        )
    }

    private fun updateRecycleViewItems(data: List<GistItem>?) {
        if (!data.isNullOrEmpty()) {
            val updatedGistList =
                gistListAdapter.currentList.plus(data.orEmpty())
            gistListAdapter.submitList(updatedGistList)
            endlessRecyclerViewScrollListener.nextPage(nextPage)
            endlessRecyclerViewScrollListener.stopPaging()
        }
    }

    private fun observeSearchPagination(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataSearchPagination.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    LOADING -> {
                        loadingAdapter.isPaging = true
                    }
                    COMPLETE -> {
                        loadingAdapter.isPaging = false
                        updateRecycleViewItems(it.data)
                    }
                    ERROR -> {
                        loadingAdapter.isPaging = false
                    }
                }
            }
        )
    }

    private fun observeFavorite(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataAddFavorite.observe(viewLifecycleOwner, {
            when (it?.status) {
                LOADING -> {
                    showProgressDialog(it.data ?: getString(R.string.dialog_loading))
                }
                SUCCESS -> {
                    dismissProgress()
                    showSnackBar(it.data)
                }
                ERROR -> {
                    dismissProgress()
                    showSnackBar(it.error?.message)
                }
            }
        })
    }

    private fun observeSearch(gifListViewModel: GistListViewModel) {
        gifListViewModel.liveDataSearch.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    LOADING -> {
                        goneViews(
                            fragment_gist_list_error,
                            custom_view_search_view.findViewById<AppCompatImageView>(R.id.search_close_btn)
                        )
                        custom_view_search_view_progress.visible()
                    }
                    COMPLETE -> {
                        goneViews(
                            custom_view_search_view_progress,
                            custom_view_search_view.findViewById<AppCompatImageView>(R.id.search_close_btn)
                        )
                        fragment_gist_list_recycler_view.visible()
                        fragment_gist_list_recycler_view.startShowAnimation()
                        if (!it.data.isNullOrEmpty()) gistListAdapter.submitList(it.data)
                    }
                    ERROR -> {
                        goneViews(
                            custom_view_search_view.findViewById<AppCompatImageView>(R.id.search_close_btn),
                            fragment_gist_list_progress_bar,
                            custom_view_search_view_progress,
                            fragment_gist_list_recycler_view
                        )
                        fragment_gist_list_error.apply {
                            type(Type.GENERIC)
                            message(it.error?.message)
                            build()
                            visible()
                        }
                    }
                }
            }
        )
    }

    override fun onFavoriteAdd(data: GistItem) {
        favoriteViewModel.addFavorite(data)
    }

    override fun onFavoriteRemove(data: GistItem) {
        favoriteViewModel.removeFavorite(data)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DETAIL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                gistListAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onGistItemClick(data: GistItem, ownerAvatar: AppCompatImageView) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity as MainActivity,
            ownerAvatar,
            ViewCompat.getTransitionName(ownerAvatar).orEmpty()
        )
        startActivityForResult(Intent(context, DetailActivity::class.java).also {
            it.putExtra(EXTRA_GIST_ITEM_VIEW, ViewCompat.getTransitionName(ownerAvatar))
            it.putExtra(EXTRA_GIST_ITEM, data)
        }, DETAIL_REQUEST_CODE, options.toBundle())
    }

    override fun onSearch(query: String) {
        lastSearch = query
        endlessRecyclerViewScrollListener.nextPage(PAGE_START)
        gistListViewModel.searchByUsername(username = query)
    }

    override fun onError(message: String) {
        lastSearch = ""
        showSnackBar(message)
    }

    override fun onTextEmpty() {
        gistListViewModel.fetchPublicGists()
    }

    override fun onErrorClickRetry() {
        gistListViewModel.fetchPublicGists()
    }
}