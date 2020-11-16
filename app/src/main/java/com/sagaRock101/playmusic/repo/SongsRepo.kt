package com.sagaRock101.playmusic.repo

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.utils.toList
import javax.inject.Inject

const val TAG = "MusicRepo"
interface SongsRepo {
    fun getSongs(): List<Song>
}

class SongsRepoImpl @Inject constructor(context: Application) : SongsRepo {
    var contentResolver: ContentResolver = context.contentResolver

    override fun getSongs(): List<Song> {
        return buildCursor().toList {
            Song.createFromCursor(this!!)
        }
    }

    private fun readMusic() {
        var cursor = buildCursor()

        if (cursor != null && cursor.moveToFirst() ) {
            var songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            do {
                var title = cursor.getString(songTitle)
                Log.e(TAG, "title: $title")
            } while (cursor.moveToNext())
        }

        cursor!!.close()
    }

    private fun buildCursor(): Cursor? {
        val selectionStatement = StringBuilder("is_music=1 AND title != ''")
//        if (!selection.isNullOrEmpty()) {
//            selectionStatement.append(" AND $selection")
//        }
        val projection = arrayOf(
            "_id",
            "title",
            "artist",
            "album",
            "duration",
            "track",
            "artist_id",
            "album_id",
            "_data"
        )

        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selectionStatement.toString(),
            null,
        MediaStore.Audio.Media.TITLE)
            ?: throw IllegalStateException("Unable to query ${MediaStore.Audio.Media.EXTERNAL_CONTENT_URI}, system returned null.")
    }



}