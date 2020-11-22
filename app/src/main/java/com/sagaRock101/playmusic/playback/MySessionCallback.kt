package com.sagaRock101.playmusic.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.sagaRock101.playmusic.player.SlidPlayer
import com.sagaRock101.playmusic.repo.SongsRepo
import com.sagaRock101.playmusic.utils.Utils
import com.sagaRock101.playmusic.utils.toMediaId
import timber.log.Timber

class MySessionCallback(
    private val mediaSession: MediaSessionCompat,
    private val mediaPlayer: SlidPlayer,
    private val songsRepo: SongsRepo
) : MediaSessionCompat.Callback() {

    override fun onPlay() {
        mediaPlayer?.playSong()
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        var songId = mediaId?.toMediaId()
        mediaPlayer.playSong(mediaId)
    }

    override fun onPause() {
        Timber.e(this.javaClass.simpleName, " onPause")
        mediaPlayer?.pauseSong()
    }

}