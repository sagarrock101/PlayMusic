package com.sagaRock101.playmusic.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.sagaRock101.playmusic.MyApplication
import com.sagaRock101.playmusic.databinding.FragmentParentTabBinding
import com.sagaRock101.playmusic.utils.Utils
import com.sagaRock101.playmusic.viewModel.MyViewModelFactory
import com.sagaRock101.playmusic.viewModel.SongViewModel
import javax.inject.Inject

class ParentTabFragment : Fragment() {
    val MY_PERMISSION_REQUEST = 1
    val TAG = this.javaClass.name

    @Inject
    lateinit var viewModel: SongViewModel

    @Inject
    lateinit var viewModelFactory: MyViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            viewModel.getSongs()
            songsObserver()
        }
    }

    private fun songsObserver() {
        viewModel.songsLD.observe(this, Observer { songs ->
            if (!songs.isNullOrEmpty()) {
                Utils.showToast(requireContext(), "songs")
                for (song in songs) {
                    Log.e(TAG, "${song.title}")
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentParentTabBinding.inflate(inflater)
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Toast.makeText(context!!, "$requestCode ", Toast.LENGTH_SHORT)
            .show()
        when (requestCode) {
            MY_PERMISSION_REQUEST -> {
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

        if (cursor != null && cursor.moveToFirst()) {
            var songTitle = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            do {
                var title = cursor.getString(songTitle)
                Log.e(TAG, "title: $title")
            } while (cursor.moveToNext())
        }

        cursor!!.close()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as MyApplication).appComponent.inject(this)
    }


}