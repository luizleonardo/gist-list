package com.example.gistlist.ui.favorites

import com.example.gistlist.R
import com.example.gistlist.ui.base.BaseFragment

class FavoritesFragment : BaseFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }

    override fun layoutResource(): Int = R.layout.fragment_favorites
}