package com.sagaRock101.playmusic.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.sagaRock101.playmusic.utils.Utils.getAlbumArtUri

@BindingAdapter("app:albumId")
fun setSongImg(
    view: ImageView,
    albumId: Long
) {
    view.clipToOutline = true
    Glide.with(view)
        .load(getAlbumArtUri(albumId))
        .transition(withCrossFade())
        .into(view)
}