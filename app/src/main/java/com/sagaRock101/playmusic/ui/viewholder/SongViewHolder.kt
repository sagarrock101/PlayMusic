package com.sagaRock101.playmusic.ui.viewholder

import com.sagaRock101.playmusic.databinding.ItemSongBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.utils.Utils
import kotlinx.android.synthetic.main.item_song.view.*

class SongViewHolder(
    var binding: ItemSongBinding,
    onItemClick: ((Song) -> Unit)?
) : BaseViewHolder<Song>(binding) {
    override fun bind(item: Song) {
        binding.song = item
    }

    init {
        itemView?.iv_control_higlighter.setOnClickListener {
            onItemClick?.invoke(binding.song!!)
        }
    }
}