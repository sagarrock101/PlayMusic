package com.sagaRock101.playmusic.ui.fragment

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentPlayerBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.interfaces.OnBackPressedListener
import com.sagaRock101.playmusic.utils.Utils
import timber.log.Timber
import java.io.InputStream
import java.lang.Exception

class PlayerFragment : Fragment(), SeekBar.OnSeekBarChangeListener,
    View.OnClickListener, MotionLayout.TransitionListener {
    private lateinit var audioVisualizer: BlobVisualizer
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    lateinit var binding: FragmentPlayerBinding

    //    private val args: PlayerFragmentArgs by navArgs()
    private val seekBarHandler = Handler()
    private var audioVisualizerColor: Int? = null
    lateinit var song: Song
    private lateinit var onBackPressedListener: OnBackPressedListener
    private var palette: Palette? = null
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

    fun setSongData(song: Song) {
        this.song = song
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.toolbar.title = ""
        binding.song = song
        binding.btnPlay.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.clPlayer.addTransitionListener(this)
        var bitmap = generateBitmap(song)
        if (bitmap != null)
            createPalette(bitmap)
        setAlbumArtColor()
        setLayoutBackgroundColor()
        startPlayer()
        initSeekBar()
        initVisualizer()
        return binding.root
    }

    private fun generateBitmap(song: Song?): Bitmap? {
        var stream: InputStream? = null
        var albumArtUri: Uri = Utils.getAlbumArtUri(song!!.albumId)
        try {
            stream = this.requireContext().contentResolver.openInputStream(albumArtUri)
        } catch (e: Exception) {

        }
        return if (stream != null)
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, albumArtUri)
        else null
    }

    private fun setLayoutBackgroundColor() {
        if (palette != null) {
            var bgLayoutColor = palette?.getLightMutedColor(
                getColor(R.color.backgroundColor)
            )
            binding.clPlayer.setBackgroundColor(bgLayoutColor!!)
            (activity as AppCompatActivity).window.apply {
                navigationBarColor = bgLayoutColor
                statusBarColor = bgLayoutColor
            }
        }
    }

    private fun createPalette(resource: Bitmap) {
        palette = createPaletteSync(resource)
    }

    private fun getColor(value: Int) =
        ResourcesCompat.getColor(requireContext().resources, value, null)

    @SuppressLint("ResourceAsColor")
    private fun setAlbumArtColor() {
        if (palette != null) {
            audioVisualizerColor = palette?.getDarkVibrantColor(
                getColor(R.color.colorAccent)
            )
        }
    }

    private fun initVisualizer() {
        audioVisualizer = binding.audioVisualizer
        var audioSession = mediaPlayer!!.audioSessionId
        if (audioSession != -1)
            audioVisualizer.setAudioSessionId(audioSession)
        audioVisualizer.setColor(getAudioVisualizerColor())
    }

    private fun changeLayoutParamsOfWidthHeight(width: Int, height: Int): ViewGroup.LayoutParams? {
        var params = audioVisualizer.layoutParams
        params.width = width
        params.height = height
        return params
    }

    private fun startPlayer() {
        val songUri = Utils.getSongUri(song!!.id)
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

    private fun getAudioVisualizerColor(): Int {
        return if (audioVisualizerColor != null)
            audioVisualizerColor!!
        else getColor(R.color.colorPrimary)
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
        when (v) {
            binding.btnPlay -> {
                updatePlayButtonWhenPlayBtnPressed(v)
            }
            binding.btnBack -> {
                onBackPressedListener.onBackPressed()
            }
        }
    }

    private fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()

    @SuppressLint("ResourceAsColor")
    private fun createPaletteAsync(bitmap: Bitmap): Int? {
        var color: Int? = R.color.colorAccent
        Palette.from(bitmap).generate { palette ->
            // Use generated instance
            color = palette?.getDominantColor(
                ResourcesCompat.getColor(
                    requireContext().resources,
                    R.color.colorAccent,
                    null
                )
            )
        }
        return color
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnBackPressedListener)
            onBackPressedListener = context
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

    }

}