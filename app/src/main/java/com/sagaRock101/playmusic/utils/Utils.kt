package com.sagaRock101.playmusic.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sagaRock101.playmusic.utils.MediaConstants.ARTWORK_URI
import com.sagaRock101.playmusic.utils.MediaConstants.SONG_URI

object Utils {
    fun showToast(ctx: Context, msg:String = "") {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT)
            .show()
    }

    fun getAlbumArtUri(albumId: Long): Uri = ContentUris.withAppendedId(ARTWORK_URI, albumId)

    fun <T : ViewDataBinding> binder(layout: Int, parent: ViewGroup): T {
        return DataBindingUtil.inflate<T>(
            LayoutInflater.from(parent?.context)!!,
            layout, parent, false)
    }

    fun getSongUri(songId: Long): Uri = ContentUris.withAppendedId(SONG_URI, songId)


}