package com.example.gistlist.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gistlist.R
import com.example.gistlist.ui.favorites.FavoritesFragment
import com.example.gistlist.ui.repos.RepositoriesFragment

val TAB_TITLES = arrayOf(
    R.string.main_activity_view_pager_tab_text_gists,
    R.string.main_activity_view_pager_tab_text_favorite
)

private const val NUM_PAGES = 2

class MainPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return NUM_PAGES
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) RepositoriesFragment.newInstance() else FavoritesFragment.newInstance()
    }
}