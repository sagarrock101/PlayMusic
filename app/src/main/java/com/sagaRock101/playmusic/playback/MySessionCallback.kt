package com.sagaRock101.playmusic.playback

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log

class MySessionCallback : MediaSessionCompat.Callback() {
    override fun onPlay() {
        Log.e(this.javaClass.simpleName, "onPlay")
    }

    override fun onSkipToNext() {
        super.onSkipToNext()
    }

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        super.onPlayFromMediaId(mediaId, extras)
    }
}