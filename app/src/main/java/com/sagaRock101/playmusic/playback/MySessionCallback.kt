package com.sagaRock101.playmusic.playback

import android.media.session.MediaSession
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import com.sagaRock101.playmusic.player.SlidPlayer
import com.sagaRock101.playmusic.repo.SongsRepo
import com.sagaRock101.playmusic.repo.SongsRepoImpl
import com.sagaRock101.playmusic.utils.toMediaId

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
    }

    override fun onPause() {
        mediaPlayer?.pauseSong()
    }

}