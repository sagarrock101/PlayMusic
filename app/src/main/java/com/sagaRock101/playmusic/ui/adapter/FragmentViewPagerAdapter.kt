package com.sagaRock101.playmusic.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sagaRock101.playmusic.ui.fragment.ListOfSongsFragment

class FragmentViewPagerAdapter(fm: FragmentManager) : SmartFragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return  when(position) {
            0 -> {
                ListOfSongsFragment()
            } else -> {
                ListOfSongsFragment()
            }
        }
    }

    override fun getCount() = 2


}