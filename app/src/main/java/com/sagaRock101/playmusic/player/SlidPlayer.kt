package com.sagaRock101.playmusic.player

import android.app.Application
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.playback.MySessionCallback
import com.sagaRock101.playmusic.repo.SongsRepo
import javax.inject.Inject

interface SlidPlayer {
    fun playSong()
    fun pauseSong()
    fun getSession(): MediaSessionCompat
}

class SlidPlayerImpl @Inject constructor(
    private val context: Application,
    private val player: SlidMusicPlayer,
    private val songsRepo: SongsRepo
) : SlidPlayer {
    private var metadataBuilder = MediaMetadataCompat.Builder()
    private var stateBuilder = createDefaultPlaybackState()
    private var mediaSession =
        MediaSessionCompat(context, context.getString(R.string.app_name)).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
            setPlaybackState(stateBuilder.build())
            setCallback(MySessionCallback(this, this@SlidPlayerImpl, songsRepo))

        }
    override fun playSong() {
        player.onPlay()
    }

    override fun pauseSong() {
        player.onPause()
    }

    override fun getSession() = mediaSession


}

private fun createDefaultPlaybackState(): PlaybackStateCompat.Builder {
    return PlaybackStateCompat.Builder().setActions(
        PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE
                or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                or PlaybackStateCompat.ACTION_SEEK_TO
    )
}

