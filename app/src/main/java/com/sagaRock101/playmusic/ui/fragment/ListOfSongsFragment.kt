package com.sagaRock101.playmusic.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
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

    private var adapter = SongAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireContext()!!,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.RECORD_AUDIO
                ),
                MY_PERMISSION_REQUEST
            )
        } else {
            if (savedInstanceState == null)
                viewModel?.getSongs()
        }
    }

    override fun initFragmentImpl() {
        songsObserver()
        setAdapter()
    }

    private fun setAdapter() {
        binding.rvSongs.adapter = adapter
        adapter?.onItemClick = {
            navigateToPlayer(it)
        }
    }

    private fun navigateToPlayer(song: Song) {
//        val action = ParentTabFragmentDirections.actionParentTabFragmentToPlayerFragment()
//        action.song = song
//        findNavController().navigate(action)
        var playerFragment = PlayerFragment()
        playerFragment.setSongData(song)
        (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
            .replace(binding.flContainer.id, playerFragment).commit()
    }

    private fun songsObserver() {
        viewModel?.songsLD?.observe(this!!, Observer { songs ->
            if (!songs.isNullOrEmpty()) {
                adapter.setItems(songs as MutableList<Song>)
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
        (requireActivity()!!.application as MyApplication).appComponent.inject(this)
    }


    override fun getLayoutId() = R.layout.fragment_list_of_songs
}