package com.example.gistlist.ui.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.gistlist.R
import com.example.gistlist.ui.ViewData
import com.example.gistlist.ui.base.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel

class RepositoriesFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): RepositoriesFragment = RepositoriesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_repositories

    private val gistListViewModel: GistListViewModel by viewModel()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    }
                    ViewData.Status.ERROR -> {
                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}