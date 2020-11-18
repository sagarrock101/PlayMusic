package com.sagaRock101.playmusic.viewModel

import com.sagaRock101.playmusic.playback.PlayBackConnection
import com.sagaRock101.playmusic.utils.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    mediaPlayBackConnection: PlayBackConnection
) : CoroutineViewModel(Main) {

}