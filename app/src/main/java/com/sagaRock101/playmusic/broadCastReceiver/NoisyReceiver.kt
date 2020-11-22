package com.sagaRock101.playmusic.broadCastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.sagaRock101.playmusic.utils.Utils
import timber.log.Timber

class NoisyReceiver(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
): BroadcastReceiver() {

    private val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val controller = MediaControllerCompat(context, sessionToken)

    private var registered = false

    fun register() {
        if (!registered) {
            context.registerReceiver(this, filter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            Utils.showToast(context, "${intent.action.toString()}")
            controller.transportControls.pause()
        }
    }
}