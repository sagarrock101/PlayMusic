package com.sagaRock101.playmusic.model

import android.content.ContentUris
import android.database.Cursor
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val id: Long = -1,
    val albumId: Long = 0,
    val artistId: Long = 0,
    val title: String = "Title",
    val artist: String = "Artist",
    val album: String = "Album",
    val duration: Int = 0,
    val trackNumber: Int = 0,
    val path: String = ""
) : Parcelable {
    companion object {
        fun createFromCursor(cursor: Cursor, album_id: Long = 0): Song {
            return Song(
                id = cursor.getLong(0),
                title = cursor.getString(1),
                artist = cursor.getString(2),
                album = cursor.getString(3),
                duration = cursor.getInt(4),
                trackNumber = cursor.getInt(5),
                artistId = cursor.getLong(6),
                albumId = if (album_id == 0L) cursor.getLong(7) else album_id,
                path = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursor.getLong(0))
                    .toString()
            )
        }
    }
}