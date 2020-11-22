package com.sagaRock101.playmusic.player

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface SlidMusicPlayer {
    fun onPlay()
    fun onPause()
    fun setSource(path: String)
    fun setSource(uri: Uri)
    fun prepare()
    fun seekTo(position: Int)
    fun release()
    fun getAudioSessionId(): Int
}

@Singleton
class SlidMusicPlayerImpl @Inject constructor(val context: Application) : SlidMusicPlayer {
    val TAG = this.javaClass.simpleName

    private var mediaPlayerBase: MediaPlayer? = null
    private val mediaPlayer: MediaPlayer
        get() {
            if(mediaPlayerBase == null) {
                mediaPlayerBase = createPlayer()
            }
            return mediaPlayerBase ?: throw IllegalStateException("Impossible")
        }

    override fun onPlay() {
        mediaPlayer.start()
    }

    override fun onPause() {
        mediaPlayer.pause()
    }

    override fun setSource(path: String) {
        try {
            mediaPlayer?.setDataSource(path)
        } catch (e: Exception) {
            Timber.e("${e.message}")
        }
    }

    override fun setSource(uri: Uri) {
        try {
            mediaPlayer?.setDataSource(this.context, uri)
        } catch (e: Exception) {
            Timber.e("${e.message}")
        }
    }

    override fun prepare() {
        mediaPlayer?.prepare()
    }

    override fun seekTo(position: Int) {
    }

    override fun release() {
        mediaPlayer?.release()
    }

    override fun getAudioSessionId() = mediaPlayer.audioSessionId

    private fun createPlayer(): MediaPlayer? {
        return MediaPlayer().apply {
            setWakeMode(this@SlidMusicPlayerImpl.context, PowerManager.PARTIAL_WAKE_LOCK)
            val attr = AudioAttributes.Builder().apply {
                setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                setUsage(AudioAttributes.USAGE_MEDIA)
            }.build()
            setAudioAttributes(attr)
        }
    }
}

