package com.sagaRock101.playmusic.ui.fragment

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentParentTabBinding
import com.sagaRock101.playmusic.ui.adapter.FragmentViewPagerAdapter

class ParentTabFragment : BaseFragment<FragmentParentTabBinding>() {

    override fun initFragmentImpl() {
        setUpTabLayoutWithViewPager()
        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
    }

    private fun setUpTabLayoutWithViewPager() {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout
        viewPager.adapter = FragmentViewPagerAdapter(childFragmentManager)
        tabLayout.setupWithViewPager(binding.viewPager)
        tabLayout.setTabTextColors(Color.WHITE, Color.WHITE)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.songs)))
    }

    override fun getLayoutId() = R.layout.fragment_parent_tab

}