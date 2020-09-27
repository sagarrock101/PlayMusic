package com.sagaRock101.playmusic.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.sagaRock101.playmusic.MyApplication
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentListOfSongsBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.adapter.SongAdapter
import com.sagaRock101.playmusic.viewModel.MyViewModelFactory
import com.sagaRock101.playmusic.viewModel.SongViewModel
import javax.inject.Inject

class ListOfSongsFragment : BaseFragment<FragmentListOfSongsBinding>() {
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
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSION_REQUEST
            )
        } else {
            viewModel.getSongs()
        }
    }

    override fun initFragmentImpl() {
        songsObserver()
    }

    private fun songsObserver() {
        viewModel.songsLD.observe(viewLifecycleOwner, Observer { songs ->
            if (!songs.isNullOrEmpty()) {
                for (song in songs) {
                    Log.e(TAG, "${song.title}")
                }
                var adapter = SongAdapter()
                adapter.setItems(songs as MutableList<Song>)
                binding.rvSongs.adapter = adapter

            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.getSongs()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity!!.application as MyApplication).appComponent.inject(this)
    }


    override fun getLayoutId() = R.layout.fragment_list_of_songs
}