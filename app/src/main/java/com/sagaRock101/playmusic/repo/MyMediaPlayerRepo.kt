package com.sagaRock101.playmusic.repo

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sagaRock101.playmusic.utils.Utils

interface MyMediaPlayerRepo {
    fun play()
    fun setSource(path: String): Boolean
    fun setSource(uri: Uri): Boolean
    fun prepare()
    fun seekTo(position: Int)
    fun isPrepared(): Boolean
    fun isPlaying(): Boolean
    fun position(): LiveData<Int>
    fun pause()
    fun stop()
    fun reset()
    fun release()
    fun getTotalDuration(): Int
}

class MediaPlayerImpl(var context: Application) : MyMediaPlayerRepo,
    MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    val TAG = "MediaPlayerImpl"
    private val mediaPlayer: MediaPlayer = createPlayer(this)
    private var isPrepared = false

    private var seekPosMLD = MutableLiveData<Int>()
    val seekPosLD: LiveData<Int> = seekPosMLD

    override fun play() {
        mediaPlayer?.start()
    }

    override fun setSource(path: String): Boolean {
        return try {
            mediaPlayer?.setDataSource(path)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun setSource(uri: Uri): Boolean {
        return try {
            mediaPlayer?.setDataSource(context, uri)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun prepare() {
        mediaPlayer?.prepareAsync()
    }

    override fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    override fun isPrepared() = isPrepared

    override fun isPlaying() = mediaPlayer?.isPlaying ?: false

    override fun position(): LiveData<Int> {
        if (mediaPlayer != null) {
            seekPosMLD.postValue(mediaPlayer?.currentPosition)
            Utils.showToast(context, "${mediaPlayer.currentPosition}")
        }
        return seekPosLD
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun stop() {
        mediaPlayer?.pause()
    }

    override fun reset() {
        mediaPlayer?.reset()
    }

    override fun release() {
        mediaPlayer?.release()
    }

    override fun getTotalDuration() = mediaPlayer.duration.div(1000)

    override fun onPrepared(mp: MediaPlayer?) {
        isPrepared = true
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        isPrepared = false
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }
}

private fun createPlayer(owner: MediaPlayerImpl): MediaPlayer {
    return MediaPlayer().apply {
        val attr = AudioAttributes.Builder().apply {
            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            setUsage(AudioAttributes.USAGE_MEDIA)
        }.build()
        setAudioAttributes(attr)
        setOnPreparedListener(owner)
        setOnCompletionListener(owner)
        setOnErrorListener(owner)
    }
}