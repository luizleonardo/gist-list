package com.example.gistlist.ui.gistList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ext.gone
import com.example.gistlist.ext.startShowAnimation
import com.example.gistlist.ext.visible
import com.example.gistlist.ui.ViewData
import com.example.gistlist.ui.base.BaseFragment
import com.example.gistlist.ui.favorites.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_repositories.*
import kotlinx.android.synthetic.main.fragment_repositories.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class RepositoriesFragment : BaseFragment(), GistListViewHolder.FavoriteCallback {

    companion object {
        @JvmStatic
        fun newInstance(): RepositoriesFragment = RepositoriesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_repositories

    private val gistListViewModel: GistListViewModel by viewModel()
    private val favoriteViewModel: FavoriteViewModel by viewModel()

    private val gistListAdapter = GistListAdapter(this@RepositoriesFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        with(gistListViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observePublicGistList(this)
        }

        with(favoriteViewModel) {
            viewLifecycleOwner.lifecycle.addObserver(this)
            observeFavorite(this)
        }

        return view
    }

    override fun setupView(view: View) {
        super.setupView(view)
        setupSnackBar(view)
        setupRecyclerView(view)
        gistListViewModel.fetchPublicGists()
    }

    private fun setupRecyclerView(view: View) {
        view.fragment_gist_list_recycler_view.apply {
            val concatAdapter = ConcatAdapter(
                gistListAdapter,
            )
            layoutManager = LinearLayoutManager(
                view.context,
                RecyclerView.VERTICAL, false
            )
            adapter = concatAdapter
        }
    }

    private fun setupSnackBar(view: View) {
        snackBar = Snackbar.make(
            view.fragment_repositories_content_holder,
            "",
            Snackbar.LENGTH_SHORT
        )
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

    override fun onFavoriteAdd(data: GistItem) {
        favoriteViewModel.addFavorite(data)
    }

    override fun onFavoriteRemove(data: GistItem) {
        favoriteViewModel.removeFavorite(data)
    }
}