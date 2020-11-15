package com.sagaRock101.playmusic.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.sagaRock101.playmusic.broadCastReceiver.NoisyReceiver
import com.sagaRock101.playmusic.playback.MySessionCallback
import com.sagaRock101.playmusic.repo.SongsRepoImpl
import com.sagaRock101.playmusic.utils.toMediaItemList
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"

class MediaPlaybackService : MediaBrowserServiceCompat() {
    val TAG = this.javaClass.simpleName
    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    lateinit var noisyReceiver: NoisyReceiver

    @Inject
    lateinit var songsRepoImpl: SongsRepoImpl

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(applicationContext, TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                        or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
            setPlaybackState(stateBuilder.build())
            setCallback(MySessionCallback())

        }
        sessionToken = mediaSession!!.sessionToken
        noisyReceiver = NoisyReceiver(this, mediaSession!!.sessionToken)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        GlobalScope.launch {
            val itemList = withContext(IO) {
                loadChildren(parentId)
            }
            result.sendResult(itemList)
        }
    }

    private fun loadChildren(parentId: String): MutableList<MediaBrowserCompat.MediaItem> {
        var list = mutableListOf<MediaBrowserCompat.MediaItem>()
        list.addAll(songsRepoImpl.getSongs().toMediaItemList())
        return list
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot(MY_MEDIA_ROOT_ID, null)
    }

}