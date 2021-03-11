package com.example.gistlist.ui.favorites

import com.example.gistlist.R
import com.example.gistlist.ui.base.BaseFragment

class FavoritesFragment : BaseFragment() {

    companion object {
        private const val COLUMNS = 2

        @JvmStatic
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_favorites
}