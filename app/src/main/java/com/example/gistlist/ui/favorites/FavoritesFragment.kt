package com.example.gistlist.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.gone
import com.example.gistlist.ext.startShowAnimation
import com.example.gistlist.ext.visible
import com.example.gistlist.ui.base.BaseFragment
import com.example.gistlist.ui.detail.DetailActivity
import com.example.gistlist.ui.gistList.GistListAdapter
import com.example.gistlist.ui.gistList.GistListFragment
import com.example.gistlist.ui.gistList.GistListViewHolder
import com.example.gistlist.ui.helper.ViewData
import com.example.gistlist.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseFragment(),
    GistListViewHolder.GistItemCallback {

    companion object {
        @JvmStatic
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_favorites

    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private val gifListAdapter = GistListAdapter(this@FavoritesFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavoritesList(this)
            observeFavorite(this)
        }

        return view
    }

    override fun setupView(view: View) {
        super.setupView(view)
        setupRecyclerView(view)
        setupSnackBar(view)
        favoriteViewModel.getFavorites()
    }

    private fun setupRecyclerView(view: View) {
        view.fragment_favorite_recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.VERTICAL, false
            )
            adapter = gifListAdapter
        }
    }

    private fun setupSnackBar(view: View) {
        snackBar = Snackbar.make(
            view.fragment_favorite_content_holder,
            "",
            Snackbar.LENGTH_SHORT
        )
    }

    private fun observeFavorite(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataAddFavorite.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    showProgressDialog(it.data ?: "Loading")
                }
                ViewData.Status.SUCCESS -> {
                    dismissProgress()
                    snackBar?.run {
                        if (!isShown) this.show()
                        this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text = it.data
                    }
                }
                ViewData.Status.ERROR -> {
                    dismissProgress()
                    snackBar?.run {
                        this.view.findViewById<AppCompatTextView>(R.id.snackbar_text).text =
                            it.error?.message
                        if (!isShown) this.show() else this.dismiss()
                    }
                }
            }
        })
    }

    private fun observeFavoritesList(favoriteViewModel: FavoriteViewModel) {
        favoriteViewModel.liveDataFavoritesList.observe(viewLifecycleOwner, {
            when (it?.status) {
                ViewData.Status.LOADING -> {
                    fragment_favorite_progress_bar.visible()
                    fragment_favorite_recycler_view.gone()
                }
                ViewData.Status.SUCCESS -> {
                    fragment_favorite_progress_bar.gone()
                    if (it.data.isNullOrEmpty()) {
                        fragment_favorite_recycler_view.gone()
                    } else {
                        fragment_favorite_recycler_view.visible()
                        fragment_favorite_recycler_view.startShowAnimation()
                        gifListAdapter.submitList(
                            it.data
                        )
                    }
                }
                ViewData.Status.ERROR -> {
                    fragment_favorite_progress_bar.gone()
                    fragment_favorite_recycler_view.gone()
                }
            }
        })
    }

    override fun onFavoriteAdd(data: GistItem) {
        favoriteViewModel.addFavorite(data)
    }

    override fun onFavoriteRemove(data: GistItem) {
        favoriteViewModel.removeFavorite(data)
    }

    override fun onGistItemClick(data: GistItem, ownerAvatar: AppCompatImageView) {
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            activity as MainActivity,
            ownerAvatar,
            ViewCompat.getTransitionName(ownerAvatar).orEmpty()
        )
        startActivityForResult(Intent(context, DetailActivity::class.java).also {
            it.putExtra(DetailActivity.EXTRA_GIST_ITEM_VIEW, ViewCompat.getTransitionName(ownerAvatar))
            it.putExtra(DetailActivity.EXTRA_GIST_ITEM, data)
        }, GistListFragment.DETAIL_REQUEST_CODE, options.toBundle())
    }

}