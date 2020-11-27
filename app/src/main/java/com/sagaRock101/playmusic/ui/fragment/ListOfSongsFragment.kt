package com.sagaRock101.playmusic.ui.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.sagaRock101.playmusic.MyApplication
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentListOfSongsBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.adapter.SongAdapter
import com.sagaRock101.playmusic.interfaces.OnSongItemClickedListener
import com.sagaRock101.playmusic.ui.viewModel.SongViewModel
import javax.inject.Inject

class ListOfSongsFragment : BaseFragment<FragmentListOfSongsBinding>() {
    val MY_PERMISSION_REQUEST = 1
    val TAG = this.javaClass.name

    @Inject
    lateinit var viewModel: SongViewModel

    private var adapter = SongAdapter()

    lateinit var listener: OnSongItemClickedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = ViewModelProvider(this, MyViewModelFactory(activity?.application!!)).get(SongViewModel::class.java)

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
            if (savedInstanceState == null) {
                viewModel?.getSongs()
            }

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        songsObserver()
    }


    override fun initFragmentImpl() {
        retainInstance = true
        setAdapter()
    }

    private fun setAdapter() {
        binding.rvSongs.adapter = adapter
        adapter?.onItemClick = {song, position ->
            navigateToPlayer(song, position)
        }
    }

    private fun navigateToPlayer(song: Song, position: Int) {
//        listener.startPlayer(song, position)

        viewModel.mediaItemClicked(song.toMediaItem(), null)
//        val action = ParentTabFragmentDirections.actionParentTabFragmentToPlayerFragment()
//        action.song = song
//        findNavController().navigate(action)
//        var playerFragment = PlayerFragment()
//        playerFragment.setSongData(song, position)
//        (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
//            .replace(binding.flContainer.id, playerFragment).commit()
    }

    private fun songsObserver() {
        viewModel?.songsLD?.observe(viewLifecycleOwner, Observer { songs ->
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
                    viewModel?.getSongs()
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity()!!.application as MyApplication).appComponent.inject(this)
    }


    override fun getLayoutId() = R.layout.fragment_list_of_songs

    fun setSongItemListener(listener: OnSongItemClickedListener) {
        this.listener = listener
    }
}