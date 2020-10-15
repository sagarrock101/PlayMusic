package com.sagaRock101.playmusic.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.utils.Utils.getAlbumArtUri
import java.io.InputStream
import java.lang.Exception

@BindingAdapter("app:albumId", "app:recycled", requireAll = false)
fun setSongImg(
    view: ImageView,
    albumId: Long,
    recycled: Boolean = false
) {
    view.clipToOutline = true
    val drawable = getDrawable(view.context, R.drawable.ic_play)
    Glide.with(view)
        .load(getAlbumArtUri(albumId))
        .transition(withCrossFade()).apply {
            if (recycled) {
            error(Glide.with(view).load(drawable))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            view.setImageDrawable(placeholder)
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            view.setImageDrawable(resource)
                        }
                    })
            } else {
                placeholder(R.drawable.ic_play)
                    .error(R.drawable.ic_play)
                    .into(view)
            }

        }

}

@BindingAdapter("app:loadRoundedAlbumImage")
fun ImageView.roundedImg(albumId: Long) {
    var stream: InputStream? = null
    try {
        stream = this.context.contentResolver.openInputStream(getAlbumArtUri(albumId))
    } catch (e: Exception) {

    }
    if(stream == null) {
        Glide.with(this.context).load(R.drawable.music_placeholder)
            .into(this)
    } else {
        Glide.with(this.context).load(getAlbumArtUri(albumId))
            .circleCrop()
            .into(this)
    }
    
}