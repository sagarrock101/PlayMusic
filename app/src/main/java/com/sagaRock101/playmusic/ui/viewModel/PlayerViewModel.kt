package com.sagaRock101.playmusic.ui.viewModel

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.sagaRock101.playmusic.model.MediaItemData
import com.sagaRock101.playmusic.playback.PlayBackConnection
import com.sagaRock101.playmusic.utils.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    playBackConnection: PlayBackConnection
) : CoroutineViewModel(Main) {

    private val playingMediaData = Observer<MediaMetadataCompat> { mediaMetaData ->
        mediaMetaData?.let {
            currentDataMLD.postValue(MediaItemData.pullMediaMetadata(mediaMetaData))
        }

    }
    private val currentDataMLD = MutableLiveData<MediaItemData>()
    val currentLD: LiveData<MediaItemData> = currentDataMLD

    private val playBackConnection = playBackConnection.also {
        it.nowPlaying.observeForever(playingMediaData)
    }


}