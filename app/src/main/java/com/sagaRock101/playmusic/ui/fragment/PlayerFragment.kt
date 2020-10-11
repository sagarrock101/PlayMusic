package com.sagaRock101.playmusic.ui.fragment

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.sagaRock101.playmusic.MyApplication
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentPlayerBinding
import com.sagaRock101.playmusic.utils.Utils
import com.sagaRock101.playmusic.viewModel.MediaPlayerViewModel
import timber.log.Timber
import javax.inject.Inject

class PlayerFragment : BaseFragment<FragmentPlayerBinding>(), SeekBar.OnSeekBarChangeListener,
    View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var seekBar: SeekBar
    private val args: PlayerFragmentArgs by navArgs()
//    private val seekBarHandler = Handler()
//    private var seekBarRunnable: Runnable = object : Runnable {
//        override fun run() {
//            if (mediaPlayer != null) {
//                var currentPos = mediaPlayer?.currentPosition?.div(1000)
//                if (currentPos != null) {
//                    seekBar.progress = currentPos
//                    Timber.e("handler: ${seekBar.progress}")
//                }
//            }
////            seekBarHandler.postDelayed(this, 50)
//        }
//    }

    @Inject
    lateinit var mediaPlayerViewModel: MediaPlayerViewModel

    override fun initFragmentImpl() {
        binding.song = args.song
        binding.btnPlay.setOnClickListener(this)
        startPlayer()
        initSeekBar()
    }

    override fun getLayoutId() = R.layout.fragment_player

    private fun startPlayer() {
        seekBar = binding.seekBar

        seekBar.setOnSeekBarChangeListener(this)
        val songUri = Utils.getSongUri(args.song!!.id)
        mediaPlayerViewModel.setupMediaPlayer(songUri)
        var totalDuration = mediaPlayerViewModel.getTotalDuration()
        Log.e(TAG, "duration: $totalDuration")
        seekBar.max = totalDuration!!
        mediaPlayerViewModel.getSeekPos().observe(this, Observer { curPos ->
            Log.e(TAG, "seekPos: $curPos")
            seekBar.progress = curPos.div(1000)
        })
//        mediaPlayer = MediaPlayer().apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .build()
//            )
//            setDataSource(requireContext(), songUri)
//            prepare()
//            start()
//        }
    }

    private fun initSeekBar() {

//        seekBarHandler.postDelayed(seekBarRunnable, 0)
    }

    override fun onDestroy() {
        mediaPlayerViewModel.release()
        super.onDestroy()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            var value = seekBar?.progress?.times(1000)
            mediaPlayerViewModel.seekToPos(value!!)
            Timber.e("onProgressChanged: ${seekBar!!.progress}")
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onPause() {
//        seekBarHandler.removeCallbacks(seekBarRunnable)
        super.onPause()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnPlay -> {
                updatePlayButtonWhenPlayBtnPressed(v)
            }
        }
    }

    private fun updatePlayButtonWhenPlayBtnPressed(v: View) {
        if (v !is Button)
            return
        if (mediaPlayerViewModel.isPlaying()) {
            v.setBackgroundResource(R.drawable.ic_play)
            mediaPlayerViewModel?.pause()
        } else {
            v.setBackgroundResource(R.drawable.ic_pause_button)
            mediaPlayerViewModel?.start()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity()!!.application as MyApplication).appComponent.inject(this)
    }
}