package com.sagaRock101.playmusic.player

import android.app.Application
import android.media.session.PlaybackState
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.sagaRock101.playmusic.R
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.playback.MySessionCallback
import com.sagaRock101.playmusic.repo.SongsRepo
import com.sagaRock101.playmusic.utils.OnPlayerPlaying
import com.sagaRock101.playmusic.utils.Utils
import com.sagaRock101.playmusic.utils.isPlaying
import timber.log.Timber
import javax.inject.Inject

interface SlidPlayer {
    fun playSong(songId: String?)
    fun playSong()
    fun pauseSong()
    fun getSession(): MediaSessionCompat
    fun updatePlaybackState(action: PlaybackStateCompat.Builder.() -> Unit)
    fun onPlayerPlaying(onPlayerPlaying: OnPlayerPlaying)
}

class SlidPlayerImpl @Inject constructor(
    private val context: Application,
    private val player: SlidMusicPlayer,
    private val songsRepo: SongsRepo
) : SlidPlayer {
    private var onPlayerPlaying: OnPlayerPlaying = {}
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

    init {

    }

    override fun playSong(songId: String?) {
        val song = songsRepo.getSong(songId!!.toLong())
        setMetaData(song)
        updatePlaybackState {
            setState(mediaSession.controller.playbackState.state, 0, 1F)
        }
        player.setSource(Utils.getSongUri(songId?.toLong()))
        player.prepare()
        updatePlaybackState {
            setState(PlaybackStateCompat.STATE_PLAYING, 0, 1f)
        }
        player.onPlay()
        playSong()
//        player.onPlay()
    }

    private fun setMetaData(song: Song?) {
        song?.let {
            val artwork = Utils.generateBitmap(context, it)
            val metaData = metadataBuilder.apply {
                putString(MediaMetadataCompat.METADATA_KEY_ALBUM, it.album)
                putString(MediaMetadataCompat.METADATA_KEY_ARTIST, it.artist)
                putString(MediaMetadataCompat.METADATA_KEY_TITLE, it.title)
                putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, it.albumId.toString())
                putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, it.id.toString())
                putLong(MediaMetadataCompat.METADATA_KEY_DURATION, it.duration.toLong())
                putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, artwork)
            }.build()
            mediaSession.setMetadata(metaData)
        }
    }

    override fun playSong() {

    }

    override fun pauseSong() {
        player.onPause()
    }

    override fun getSession() = mediaSession

    override fun updatePlaybackState(action: PlaybackStateCompat.Builder.() -> Unit) {
        action.invoke(stateBuilder)
        setPlaybackState(stateBuilder.build())
    }

    override fun onPlayerPlaying(onPlayerPlaying: OnPlayerPlaying) {
        this.onPlayerPlaying = onPlayerPlaying
    }

    private fun setPlaybackState(playbackStateCompat: PlaybackStateCompat?) {
        mediaSession.setPlaybackState(playbackStateCompat)
        if(playbackStateCompat!!.isPlaying) {
            Timber.e("playing")
            onPlayerPlaying.invoke(true)
        } else {
            Timber.e("Not playing")
            onPlayerPlaying.invoke(false)
        }
    }


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

