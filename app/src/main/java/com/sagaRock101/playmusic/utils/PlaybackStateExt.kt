package com.sagaRock101.playmusic.utils

import android.support.v4.media.session.PlaybackStateCompat

inline val PlaybackStateCompat.isPlaying
    get() = (state == PlaybackStateCompat.STATE_PLAYING)
            || (state == PlaybackStateCompat.STATE_BUFFERING)