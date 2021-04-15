package com.example.gistlist.ui.gistList

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.gone
import com.example.gistlist.ext.startShowAnimation
import com.example.gistlist.ext.visible
import com.example.gistlist.ui.base.BaseFragment
import com.example.gistlist.ui.detail.DetailActivity
import com.example.gistlist.ui.detail.DetailActivity.Companion.EXTRA_GIST_ITEM
import com.example.gistlist.ui.detail.DetailActivity.Companion.EXTRA_GIST_ITEM_VIEW
import com.example.gistlist.ui.favorites.FavoriteViewModel
import com.example.gistlist.ui.helper.ViewData
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
    HeaderViewHolder.SearchViewCallback {

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

    private var lastSearch: String? = ""

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
                    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
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
            adapter = ConcatAdapter(headerAdapter, gistListAdapter)
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

    private fun showSnackbar(message: String?) {
        snackBar?.run {
            if (!isShown) this.show() else this.dismiss()
            this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text = message
        }
    }

    private fun observePublicGistList(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataGists.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        fragment_gist_list_progress_bar.visible()
                        fragment_gist_list_recycler_view.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        fragment_gist_list_progress_bar.gone()
                        fragment_gist_list_recycler_view.visible()
                        fragment_gist_list_recycler_view.startShowAnimation()
                        endlessRecyclerViewScrollListener.resetState()
                        gistListAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        fragment_gist_list_progress_bar.gone()
                        fragment_gist_list_recycler_view.gone()
                    }
                }
            }
        )
    }

    private fun observePublicGistListPagination(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataGistsPagination.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        fragment_gist_list_progress_bar.visible()
                    }
                    ViewData.Status.COMPLETE -> {
                        fragment_gist_list_progress_bar.gone()
                        val updatedGistList = gistListAdapter.currentList.plus(it.data.orEmpty())
                        gistListAdapter.submitList(updatedGistList)
                    }
                    ViewData.Status.ERROR -> {
                        fragment_gist_list_progress_bar.gone()
                    }
                }
            }
        )
    }

    private fun observeSearchPagination(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataSearchPagination.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        fragment_gist_list_progress_bar.visible()
                    }
                    ViewData.Status.COMPLETE -> {
                        fragment_gist_list_progress_bar.gone()
                        val updatedGistList = gistListAdapter.currentList.plus(it.data.orEmpty())
                        gistListAdapter.submitList(updatedGistList)
                    }
                    ViewData.Status.ERROR -> {
                        fragment_gist_list_progress_bar.gone()
                    }
                }
            }
        )
    }

    private fun observeFavorite(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataAddFavorite.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    showProgressDialog(it.data ?: getString(R.string.dialog_loading))
                }
                ViewData.Status.SUCCESS -> {
                    dismissProgress()
                    showSnackbar(it.data)
                }
                ViewData.Status.ERROR -> {
                    dismissProgress()
                    showSnackbar(it.error?.message)
                }
            }
        })
    }

    private fun observeSearch(gifListViewModel: GistListViewModel) {
        gifListViewModel.liveDataSearch.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        custom_view_search_view_progress.visible()
                        custom_view_search_view.findViewById<AppCompatImageView>(R.id.search_close_btn)
                            ?.gone()
                    }
                    ViewData.Status.COMPLETE -> {
                        custom_view_search_view_progress.gone()
                        custom_view_search_view.findViewById<AppCompatImageView>(R.id.search_close_btn)
                            ?.gone()
                        fragment_gist_list_recycler_view.visible()
                        fragment_gist_list_recycler_view.startShowAnimation()
                        endlessRecyclerViewScrollListener.resetState()
                        gistListAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        custom_view_search_view.findViewById<AppCompatImageView>(R.id.search_close_btn)
                            ?.gone()
                        custom_view_search_view_progress.gone()
                        fragment_gist_list_recycler_view.gone()
                        fragment_gist_list_progress_bar.gone()
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
        gistListViewModel.searchByUsername(username = query)
    }

    override fun onError(message: String) {
        showSnackbar(message)
    }

    override fun onTextEmpty() {
        gistListViewModel.fetchPublicGists()
    }
}