package com.sagaRock101.playmusic.viewModel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sagaRock101.playmusic.model.Song
import com.sagaRock101.playmusic.repo.SongsRepoImpl
import com.sagaRock101.playmusic.utils.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongViewModel @Inject constructor(application: Application) :
    CoroutineViewModel(Dispatchers.Main) {

    @Inject
    lateinit var songsRepo: SongsRepoImpl

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