package com.sagaRock101.playmusic.utils

import android.support.v4.media.MediaBrowserCompat
import com.sagaRock101.playmusic.model.Song

fun List<Song>.toMediaItemList(): List<MediaBrowserCompat.MediaItem> {
    return this.map { it.toMediaItem() }
}

//fun List<MediaItem>?.toIDList(): LongArray {
//    return this?.map { it._id }?.toLongArray() ?: LongArray(0)
//}
