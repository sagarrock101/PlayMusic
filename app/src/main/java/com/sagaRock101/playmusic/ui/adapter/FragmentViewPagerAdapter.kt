package com.sagaRock101.playmusic.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.sagaRock101.playmusic.ui.fragment.ListOfSongsFragment
import com.sagaRock101.playmusic.ui.interfaces.OnSongItemClickedListener

class FragmentViewPagerAdapter(fm: FragmentManager, var listener: OnSongItemClickedListener?) : SmartFragmentStatePagerAdapter(fm) {
    //TODO: need to refactor
    override fun getItem(position: Int): Fragment {
        var frag1 = ListOfSongsFragment()
        var frag2 = ListOfSongsFragment()
        if(listener != null) {
            frag1.setSongItemListener(listener!!)
            frag2.setSongItemListener(listener!!)
        }

        return  when(position) {
            0 -> {
                frag1
            } else -> {
                frag1
            }
        }
    }

    override fun getCount() = 1


}