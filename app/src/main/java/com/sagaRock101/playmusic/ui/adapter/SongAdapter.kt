package com.sagaRock101.playmusic.ui.adapter

import android.view.ViewGroup
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.ItemSongBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.viewholder.BaseViewHolder
import com.sagaRock101.playmusic.ui.viewholder.SongViewHolder
import com.sagaRock101.playmusic.utils.Utils

class SongAdapter : BaseAdapter<Song>() {
    var layout = R.layout.item_song
    var onItemClick: ((Song) -> Unit)? = null
    override fun getLayoutId(position: Int, obj: Song) = layout

    override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<Song> {
        val binding = Utils.binder<ItemSongBinding>(layout, parent)
        return SongViewHolder(binding, onItemClick)
    }

}