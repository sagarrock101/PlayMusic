package com.sagaRock101.playmusic.utils

import android.net.Uri
import android.provider.MediaStore

object MediaConstants {
    val ARTWORK_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    val SONG_URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
}