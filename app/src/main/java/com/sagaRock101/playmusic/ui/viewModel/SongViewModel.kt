package com.sagaRock101.playmusic.ui.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.ui.repo.SongsRepo
import com.sagaRock101.playmusic.ui.repo.SongsRepoImpl
import com.sagaRock101.playmusic.utils.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SongViewModel(
    ctx: Context
) : CoroutineViewModel(Dispatchers.Main) {
    var songsRepo: SongsRepo = SongsRepoImpl(ctx)

    private val _songsMLD = MutableLiveData<List<Song>>()
    val songsLD: LiveData<List<Song>> = _songsMLD
    fun getSongs() {
        launch {
            val songs = withContext(IO) {
                songsRepo.getSongs()
            }
            if (!songs.isNullOrEmpty()) {
                _songsMLD.postValue(songs)
            }
        }
    }
}