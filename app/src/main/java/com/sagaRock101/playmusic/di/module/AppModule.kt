package com.sagaRock101.playmusic.di.module

import android.app.Application
import com.sagaRock101.playmusic.player.SlidMusicPlayer
import com.sagaRock101.playmusic.player.SlidMusicPlayerImpl
import com.sagaRock101.playmusic.player.SlidPlayer
import com.sagaRock101.playmusic.player.SlidPlayerImpl
import com.sagaRock101.playmusic.repo.SongsRepo
import com.sagaRock101.playmusic.repo.SongsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    var application: Application? = null


    @Provides
    fun provideRepositoryImpl(application: Application): SongsRepo {
        this.application = application
        return SongsRepoImpl(application)
    }

    @Provides
    fun provideSlidMusicPlayer(): SlidMusicPlayer {
        return SlidMusicPlayerImpl(application!!)
    }

    @Provides
    fun provideSlidPlayer(): SlidPlayer {
        return SlidPlayerImpl(
            application!!,
            provideSlidMusicPlayer(),
            provideRepositoryImpl(application!!)
        )
    }

}