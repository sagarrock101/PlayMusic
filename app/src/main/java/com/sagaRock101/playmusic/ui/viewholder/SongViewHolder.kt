package com.sagaRock101.playmusic.ui.viewholder

import com.sagaRock101.playmusic.databinding.ItemSongBinding
import com.sagaRock101.playmusic.model.Song

class SongViewHolder(var binding: ItemSongBinding) : BaseViewHolder<Song>(binding) {
    override fun bind(item: Song) {
        binding.song = item
    }
}