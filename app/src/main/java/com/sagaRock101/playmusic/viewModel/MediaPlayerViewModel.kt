package com.sagaRock101.playmusic.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import com.sagaRock101.playmusic.repo.MediaPlayerImpl
import com.sagaRock101.playmusic.utils.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlayerViewModel @Inject constructor(application: Application) :
    CoroutineViewModel(Dispatchers.Main) {

    @Inject
    lateinit var mediaPlayerRepo: MediaPlayerImpl

    fun setupMediaPlayer(uri: Uri) {
        mediaPlayerRepo.setSource(uri)
        mediaPlayerRepo.prepare()
        mediaPlayerRepo.play()
    }

    fun stop() {
        mediaPlayerRepo.stop()
    }

    fun seekToPos(pos: Int) {
        mediaPlayerRepo.seekTo(pos)
    }

    fun pause() {
        mediaPlayerRepo.stop()
    }

    fun start() {
        mediaPlayerRepo.play()
    }

    fun getTotalDuration() = mediaPlayerRepo.getTotalDuration()

    fun release() {
        mediaPlayerRepo.release()
    }

    fun isPlaying() = mediaPlayerRepo.isPlaying()

    fun getSeekPos(): LiveData<Int> {
       return mediaPlayerRepo.position()
    }

}