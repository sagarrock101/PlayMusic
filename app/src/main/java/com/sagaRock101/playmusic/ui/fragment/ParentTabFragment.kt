package com.sagaRock101.playmusic.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sagaRock101.playmusic.databinding.FragmentParentTabBinding
import com.sagaRock101.playmusic.model.AudioModel
import timber.log.Timber
import java.util.jar.Manifest

class ParentTabFragment : Fragment() {
    val MY_PERMISSION_REQUEST = 1
    val TAG = this.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Timber.e("Files length: ${getAllAudioFromDevice(context!!)!!.size}")
        if (ContextCompat.checkSelfPermission(
                activity!!,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST
            )
        } else {
            readMusic()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Toast.makeText(context!!, "$requestCode ", Toast.LENGTH_SHORT)
            .show()
        when (requestCode) {
            MY_PERMISSION_REQUEST-> {
                Toast.makeText(context!!, "permission granted ", Toast.LENGTH_SHORT)
                    .show()
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context!!, "permission granted ", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun readMusic() {
        var contentResolver = context!!.contentResolver
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        var cursor = contentResolver.query(songUri, projection, null, null, null)



        if (cursor != null && cursor.moveToFirst() ) {
            var songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            do {
                var title = cursor.getString(songTitle)
                Log.e(TAG, "title: $title")
            } while (cursor.moveToNext())
        }

        cursor!!.close()
    }

    private val binding by viewBinding(FragmentParentTabBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    private fun getAllAudioFromDevice(context: Context): List<AudioModel>? {

        Toast.makeText(context, "check", Toast.LENGTH_SHORT)
            .show()
        val tempAudioList: MutableList<AudioModel> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val c: Cursor? = context.contentResolver.query(
            uri,
            projection,
            MediaStore.Audio.Media.DATA + " like ? ",
            arrayOf("%music%"),
            null
        )

        if (c != null) {

            while (c.moveToNext()) {
                val audioModel = AudioModel()
                val path: String = c.getString(0)
                val name: String = c.getString(1)
                val album: String = c.getString(2)
                val artist: String = c.getString(3)
                audioModel.setaName(name)
                audioModel.setaAlbum(album)
                audioModel.setaArtist(artist)
                audioModel.setaPath(path)
                Log.e("Name :$name", " Album :$album")
                Log.e("Path :$path", " Artist :$artist")
                tempAudioList.add(audioModel)
            }
            c.close()
        }
        return tempAudioList
    }
}