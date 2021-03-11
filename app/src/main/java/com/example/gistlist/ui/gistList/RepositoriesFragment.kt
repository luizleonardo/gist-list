package com.example.gistlist.ui.gistList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gistlist.R
import com.example.gistlist.data.entities.GistItem
import com.example.gistlist.ui.ViewData
import com.example.gistlist.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_repositories.view.*
import org.koin.android.viewmodel.ext.android.viewModel

class RepositoriesFragment : BaseFragment(), GistListViewHolder.FavoriteCallback {

    companion object {
        @JvmStatic
        fun newInstance(): RepositoriesFragment = RepositoriesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_repositories

    private val gistListViewModel: GistListViewModel by viewModel()

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

        return view
    }

    override fun setupView(view: View) {
        super.setupView(view)
        view.fragment_gist_list_recycler_view.apply {
            val concatAdapter = ConcatAdapter(
                gistListAdapter,
            )
            layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
            adapter = concatAdapter
        }
        gistListViewModel.fetchPublicGists()
    }

    private fun observePublicGistList(gistListViewModel: GistListViewModel) {
        gistListViewModel.liveDataGists.observe(
            viewLifecycleOwner, {
                when (it?.status) {
                    ViewData.Status.LOADING -> {
                        Toast.makeText(context, "LOADING", Toast.LENGTH_SHORT).show()
                    }
                    ViewData.Status.COMPLETE -> {
                        Toast.makeText(context, "COMPLETE", Toast.LENGTH_SHORT).show()
                        gistListAdapter.submitList(it.data)
                    }
                    ViewData.Status.ERROR -> {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    override fun onFavoriteAdd(data: GistItem) {
    }

    override fun onFavoriteRemove(data: GistItem) {
    }
}