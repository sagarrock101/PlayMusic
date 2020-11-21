package com.sagaRock101.playmusic.di.module

import android.app.Application
import android.content.ComponentName
import com.sagaRock101.playmusic.playback.PlayBackConnection
import com.sagaRock101.playmusic.playback.PlaybackConnectionImp
import com.sagaRock101.playmusic.player.SlidMusicPlayer
import com.sagaRock101.playmusic.player.SlidMusicPlayerImpl
import com.sagaRock101.playmusic.player.SlidPlayer
import com.sagaRock101.playmusic.player.SlidPlayerImpl
import com.sagaRock101.playmusic.repo.SongsRepo
import com.sagaRock101.playmusic.repo.SongsRepoImpl
import com.sagaRock101.playmusic.service.MediaPlaybackService
import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class AppModule {
    var application: Application? = null

    @Provides
    fun provideRepositoryImpl(application: Application): SongsRepo {
        return SongsRepoImpl(application)
    }

    @Provides
    fun provideSlidMusicPlayer(application: Application): SlidMusicPlayer {
        return SlidMusicPlayerImpl(application)
    }

    @Provides
    fun provideSlidPlayer(application: Application): SlidPlayer {
        return SlidPlayerImpl(
            application!!,
            provideSlidMusicPlayer(application),
            provideRepositoryImpl(application!!)
        )
    }

    @Provides
    fun provideServiceComponentName(application: Application): ComponentName {
        return ComponentName(application, MediaPlaybackService::class.java)
    }

    @Provides
    fun providePlaybackConnectionImpl(application: Application): PlayBackConnection {
        return PlaybackConnectionImp(application, provideServiceComponentName(application))
    }

}