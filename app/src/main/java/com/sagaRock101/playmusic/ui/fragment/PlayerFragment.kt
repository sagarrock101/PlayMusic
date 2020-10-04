package com.sagaRock101.playmusic.ui.fragment

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.navigation.fragment.navArgs
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentPlayerBinding
import com.sagaRock101.playmusic.utils.Utils

class PlayerFragment : BaseFragment<FragmentPlayerBinding>() {
    private val args: PlayerFragmentArgs by navArgs()
    override fun initFragmentImpl() {
        binding.song = args.song
        startPlayer()
    }

    override fun getLayoutId() = R.layout.fragment_player

    private fun startPlayer() {
        val songUri = Utils.getSongUri(args.song!!.id)
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(requireContext(), songUri)
            prepare()
            start()
        }
    }

}