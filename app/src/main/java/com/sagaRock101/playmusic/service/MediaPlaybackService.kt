package com.sagaRock101.playmusic.service

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import com.sagaRock101.playmusic.MyApplication
import com.sagaRock101.playmusic.broadCastReceiver.NoisyReceiver
import com.sagaRock101.playmusic.player.SlidMusicPlayer
import com.sagaRock101.playmusic.player.SlidPlayer
import com.sagaRock101.playmusic.repo.SongsRepo
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
//    private var mediaSession: MediaSessionCompat? = null
//    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    lateinit var noisyReceiver: NoisyReceiver

    @Inject
    lateinit var songRepo: SongsRepo

    @Inject
    lateinit var slidPlayer: SlidPlayer

    override fun onCreate() {
        super.onCreate()
        (application as MyApplication).appComponent.inject(this)
        noisyReceiver = NoisyReceiver(this, slidPlayer.getSession().sessionToken)
        sessionToken = slidPlayer.getSession().sessionToken
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
        list.addAll(songRepo.getSongs().toMediaItemList())
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