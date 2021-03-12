package com.example.gistlist.ui.main

import com.example.gistlist.R
import com.example.gistlist.ui.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun layoutResource(): Int = R.layout.activity_main

    override fun setupView() {
        setupViewPager()
    }

    private fun setupViewPager() {
        main_activity_view_pager?.let {
            it.adapter = MainPagerAdapter(this@MainActivity)
            TabLayoutMediator(main_activity_tab_layout, it) { tab, position ->
                tab.text = getString(TAB_TITLES[position])
            }.attach()
        }
    }
}