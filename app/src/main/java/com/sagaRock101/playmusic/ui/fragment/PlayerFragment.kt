package com.sagaRock101.playmusic.ui.fragment

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.palette.graphics.Palette
import com.gauravk.audiovisualizer.visualizer.BlobVisualizer
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.databinding.FragmentPlayerBinding
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.interfaces.OnBackPressedListener
import com.sagaRock101.playmusic.interfaces.PlayerControlsListener
import com.sagaRock101.playmusic.service.MediaPlaybackService
import com.sagaRock101.playmusic.utils.Utils
import timber.log.Timber
import java.io.InputStream

class PlayerFragment() : BaseFragment<FragmentPlayerBinding>(), SeekBar.OnSeekBarChangeListener,
    View.OnClickListener, MotionLayout.TransitionListener {
//    private lateinit var mediaController: MediaControllerCompat
    private var volumeControlStream: Int = 0
    private var bgLayoutColor: Int? = null
    private var audioVisualizer: BlobVisualizer? = null
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    private var transFlag: Boolean = false
    private val seekBarHandler = Handler()
    private var audioVisualizerColor: Int? = null
    lateinit var song: Song
    private lateinit var onBackPressedListener: OnBackPressedListener
    private var palette: Palette? = null
    private var landScapeFlag = false
    private lateinit var playerControlsListener: PlayerControlsListener
//    private lateinit var mediaBrowser: MediaBrowserCompat

//    private var controllerCallback = object : MediaControllerCompat.Callback() {
//
//        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
//
//        }
//
//        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
//
//        }
//
//        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
//
//        }
//
//        override fun onSessionDestroyed() {
//
//        }
//    }

//    private val connectionCallbacks = object : MediaBrowserCompat.ConnectionCallback() {
//        override fun onConnected() {
//            mediaBrowser.sessionToken.also { token ->
//                 mediaController = MediaControllerCompat(
//                    requireActivity(),
//                    token
//                )
//
//                Utils.showToast(requireContext(), "$token")
//                MediaControllerCompat.setMediaController(requireActivity(), mediaController)
//            }
////            mediaController.registerCallback(controllerCallback)
//        }
//
//        override fun onConnectionSuspended() {
//
//        }
//
//        override fun onConnectionFailed() {
//
//        }
//
//    }

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

    fun setPlayerControlListener(listener: PlayerControlsListener) {
        playerControlsListener = listener
    }

    override fun getLayoutId() = R.layout.fragment_player

    override fun initFragmentImpl() {
        binding.song = song
        binding.btnPlay.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
        binding.clPlayer.addTransitionListener(this)
        binding.ivBackward.setOnClickListener(this)
        binding.ivForward.setOnClickListener(this)
        var bitmap = generateBitmap(song)
        if (bitmap != null)
            createPalette(bitmap)
        setAlbumArtColor()
        setLayoutBackgroundColor()
//        startPlayer()
//        initSeekBar()
//        initVisualizer()
        if (transFlag)
            makeTransitionToExpanded()
        else
            makeTransitionToCollapse()
        retainInstance = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mediaBrowser = MediaBrowserCompat(
//            requireContext(),
//            ComponentName(requireContext(), MediaPlaybackService::class.java),
//            connectionCallbacks,
//            null
//        )
    }

    override fun onStart() {
        super.onStart()
//        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
//        MediaControllerCompat.getMediaController(requireActivity())
//            .unregisterCallback(controllerCallback)
//        mediaBrowser.disconnect()
    }

    fun setSongData(song: Song, itemPosition: Int) {
        this.song = song
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
            bgLayoutColor = palette?.getLightMutedColor(
                getColor(R.color.backgroundColor)
            )
            binding.viewBg2.setBackgroundColor(bgLayoutColor!!)
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
//        if (audioVisualizer == null) {
        try {
            audioVisualizer = binding.audioVisualizer
            var audioSession = mediaPlayer!!.audioSessionId
            if (audioSession != -1)
                audioVisualizer?.setAudioSessionId(audioSession)
            audioVisualizer?.setColor(getAudioVisualizerColor())
        } catch (e: Exception) {

        }
//        }
    }

    private fun startPlayer() {
        val songUri = Utils.getSongUri(song!!.id)
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
        }

        try {
            mediaPlayer?.apply {
                setDataSource(requireContext(), songUri)
                prepare()
//                start()
            }
        } catch (e: Exception) {
            releaseVisualizer()
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
//            mediaPlayer?.pause()
        } else {
            v.setBackgroundResource(R.drawable.ic_pause_button)
//            mediaPlayer?.start()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnPlay -> {
                updatePlayButtonWhenPlayBtnPressed(v)
            }
            binding.btnBack -> {
                makeTransitionToCollapse()
            }
            binding.ivBackward -> {

            }
            binding.ivForward -> {

            }
        }
    }

    fun makeTransitionToCollapse() {
        binding.clPlayer.transitionToStart()
    }

    fun makeTransitionToExpanded() {
        binding.clPlayer.transitionToEnd()
        changeNavBarStatusBarColor()
    }

    fun setMotionLayoutTransFlag(transFlag: Boolean) {
        this.transFlag = transFlag
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

    override fun onTransitionChange(
        motionLayout: MotionLayout?,
        collapsed: Int,
        expanded: Int,
        progress: Float
    ) {
        if (motionLayout?.progress!! < 1.0f) {
            (activity as AppCompatActivity).window.apply {
                navigationBarColor = Utils.getColor(context, R.color.colorPrimaryDark)
                statusBarColor = Utils.getColor(context, R.color.colorPrimaryDark)
            }
            transFlag = false
        }
    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, state: Int) {
        if (state == R.id.expanded) {
            if (bgLayoutColor != null) {
                changeNavBarStatusBarColor()
            }
        }
    }

    private fun changeNavBarStatusBarColor() {
        (activity as AppCompatActivity).window.apply {
            navigationBarColor = bgLayoutColor!!
            statusBarColor = bgLayoutColor!!
        }
    }

    fun releaseVisualizer() {
        audioVisualizer?.release()
    }

    fun resetPlayer() {
        mediaPlayer?.let { it.reset() }
    }

    fun getMotionLayout() = binding.clPlayer

    fun setLandScapeFlag(landScapeFlag: Boolean) {
        this.landScapeFlag = landScapeFlag
    }
}