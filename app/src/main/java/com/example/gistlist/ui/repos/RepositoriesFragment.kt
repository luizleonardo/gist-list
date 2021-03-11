package com.example.gistlist.ui.repos

import com.example.gistlist.R
import com.example.gistlist.ui.base.BaseFragment

class RepositoriesFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): RepositoriesFragment = RepositoriesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_repositories
}