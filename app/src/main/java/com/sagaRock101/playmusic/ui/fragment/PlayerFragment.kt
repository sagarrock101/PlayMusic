package com.sagaRock101.playmusic.ui.fragment

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.navigation.fragment.navArgs
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentPlayerBinding
import com.sagaRock101.playmusic.utils.Utils
import kotlinx.android.synthetic.main.fragment_player.*
import timber.log.Timber

class PlayerFragment : BaseFragment<FragmentPlayerBinding>(), SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    private val args: PlayerFragmentArgs by navArgs()
    private val seekBarHandler = Handler()
    private var seekBarRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                var currentPos = mediaPlayer?.currentPosition?.div(1000)
                if (currentPos != null) {
                    seekBar.progress = currentPos
                    Timber.e("handler: ${seekBar.progress}")
                }
            }
            seekBarHandler.postDelayed(this, 50)
        }
    }
    override fun initFragmentImpl() {
        binding.song = args.song
        binding.btnPlay.setOnClickListener(this)
        startPlayer()
        initSeekBar()
    }

    override fun getLayoutId() = R.layout.fragment_player

    private fun startPlayer() {
        val songUri = Utils.getSongUri(args.song!!.id)
        mediaPlayer = MediaPlayer().apply {
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

    private fun initSeekBar() {
        seekBar = binding.seekBar
        var totalDuration = mediaPlayer?.duration?.div(1000)
        seekBar.max = totalDuration!!
        seekBar.setOnSeekBarChangeListener(this)

        seekBarHandler.postDelayed(seekBarRunnable, 0)

    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (mediaPlayer != null && fromUser) {
            var value = seekBar?.progress?.times(1000)
            mediaPlayer?.seekTo(value!!)
            Timber.e("onProgressChanged: ${seekBar!!.progress}")
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onPause() {
        seekBarHandler.removeCallbacks(seekBarRunnable)
        super.onPause()
    }

    private fun updatePlayButtonWhenPlayBtnPressed(v: View) {
        if (v !is Button)
            return
        if (mediaPlayer!!.isPlaying) {
            v.setBackgroundResource(R.drawable.ic_play)
            mediaPlayer?.pause()
        } else {
            v.setBackgroundResource(R.drawable.ic_pause_button)
            mediaPlayer?.start()
        }
    }

    override fun onClick(v: View?) {
        when(v) {
            binding.btnPlay -> {
                updatePlayButtonWhenPlayBtnPressed(v)
            }
        }
    }

}