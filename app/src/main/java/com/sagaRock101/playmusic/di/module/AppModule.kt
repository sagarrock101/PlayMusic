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

    @Provides
    fun provideServiceComponentName(): ComponentName {
        return ComponentName(application!!, MediaPlaybackService::class.java)
    }

    @Provides
    fun providePlaybackConnectionImpl(): PlayBackConnection {
        return PlaybackConnectionImp(application!!, provideServiceComponentName())
    }

}