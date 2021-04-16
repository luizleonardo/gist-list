package com.example.gistlist.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ui.base.BaseFragment
import com.example.gistlist.ui.detail.DetailActivity
import com.example.gistlist.ui.gistList.GistListAdapter
import com.example.gistlist.ui.gistList.GistListFragment
import com.example.gistlist.ui.gistList.GistListViewHolder
import com.example.gistlist.ui.helper.*
import com.example.gistlist.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.fragment_favorites.view.*
import kotlinx.android.synthetic.main.fragment_gist_list.*
import org.koin.android.viewmodel.ext.android.viewModel

class FavoritesFragment : BaseFragment(),
    GistListViewHolder.GistItemCallback,
    CustomViewError.Callback {

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
        fragment_favorite_error.click(this@FavoritesFragment)
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
                    showProgressDialog(it.data ?: getString(R.string.dialog_loading))
                }
                ViewData.Status.SUCCESS -> {
                    dismissProgress()
                    snackBar?.run {
                        if (!isShown) this.show()
                        snackBarTextView()?.text = it.data
                    }
                }
                ViewData.Status.ERROR -> {
                    dismissProgress()
                    snackBar?.run {
                        snackBarTextView()?.text = it.error?.message
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
                    goneViews(fragment_favorite_error, fragment_favorite_recycler_view)
                }
                ViewData.Status.SUCCESS -> {
                    goneViews(fragment_favorite_error, fragment_favorite_progress_bar)
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
                    goneViews(fragment_favorite_progress_bar, fragment_favorite_recycler_view)
                    fragment_favorite_error.apply {
                        type(CustomViewError.Type.GENERIC)
                        message(it.error?.message)
                        build()
                        visible()
                    }
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
            it.putExtra(
                DetailActivity.EXTRA_GIST_ITEM_VIEW,
                ViewCompat.getTransitionName(ownerAvatar)
            )
            it.putExtra(DetailActivity.EXTRA_GIST_ITEM, data)
        }, GistListFragment.DETAIL_REQUEST_CODE, options.toBundle())
    }

    override fun onErrorClickRetry() {
        favoriteViewModel.getFavorites()
    }
}