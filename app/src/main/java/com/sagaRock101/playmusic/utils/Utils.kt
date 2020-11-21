package com.sagaRock101.playmusic.utils

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.utils.MediaConstants.ARTWORK_URI
import com.sagaRock101.playmusic.utils.MediaConstants.SONG_URI
import java.io.InputStream

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

    fun getColor(context: Context, value: Int) = ResourcesCompat.getColor(context.resources, value, null)

    fun generateBitmap(context: Context, song: Song?): Bitmap? {
        var stream: InputStream? = null
        var albumArtUri: Uri = Utils.getAlbumArtUri(song!!.albumId)
        try {
            stream = context.contentResolver.openInputStream(albumArtUri)
        } catch (e: Exception) {

        }
        return if (stream != null)
            MediaStore.Images.Media.getBitmap(context.contentResolver, albumArtUri)
        else null
    }

}