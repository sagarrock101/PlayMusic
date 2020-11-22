package com.sagaRock101.playmusic.playback

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.MutableLiveData
import timber.log.Timber
import javax.inject.Inject

val NONE_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

val NONE_PLAYING_STATE: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

interface PlayBackConnection {
    val isConnected: MutableLiveData<Boolean>
    val playbackState: MutableLiveData<PlaybackStateCompat>
    val lastPlayed: MutableLiveData<MediaMetadataCompat>
    val nowPlaying: MutableLiveData<MediaMetadataCompat>
    val transportControls: MediaControllerCompat.TransportControls?
    val mediaController: MediaControllerCompat?
}

class PlaybackConnectionImp @Inject constructor(
    application: Application,
    serviceComponent: ComponentName
) : PlayBackConnection {
    override val isConnected: MutableLiveData<Boolean> = MutableLiveData()
    override val playbackState: MutableLiveData<PlaybackStateCompat> = MutableLiveData()
    override val lastPlayed: MutableLiveData<MediaMetadataCompat> = MutableLiveData()
    override val nowPlaying: MutableLiveData<MediaMetadataCompat> = MutableLiveData()
    override var mediaController: MediaControllerCompat? = null

    private val mediaConnectionCallback = MediaBrowserConnectionCallback(application)
    private val mediaBrowser = MediaBrowserCompat(
        application,
        serviceComponent,
        mediaConnectionCallback,
        null
    ).apply {
        connect()
    }
    override val transportControls: MediaControllerCompat.TransportControls?
        get() = mediaController?.transportControls

    private inner class MediaBrowserConnectionCallback(private val context: Context) :
        MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaControllerCallback())
            }
            isConnected.postValue(true)
        }

        override fun onConnectionSuspended() {
            isConnected.postValue(false)
        }

        override fun onConnectionFailed() {
            isConnected.postValue(false)
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            playbackState?.postValue(state ?: NONE_PLAYBACK_STATE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            metadata ?: return
            Timber.e("${metadata}")
            nowPlaying.postValue(metadata)
            lastPlayed.postValue(nowPlaying?.value ?: NONE_PLAYING_STATE)
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
        }

        override fun onSessionDestroyed() {
            mediaConnectionCallback.onConnectionSuspended()
        }
    }
}
