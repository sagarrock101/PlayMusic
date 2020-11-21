package com.sagaRock101.playmusic.ui.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sagaRock101.playmusic.playback.PlayBackConnection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyViewModelFactory @Inject constructor(
    var application: Application,
    var playBackConnection: PlayBackConnection
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
            return SongViewModel(application, playBackConnection) as T
        }
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playBackConnection) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}