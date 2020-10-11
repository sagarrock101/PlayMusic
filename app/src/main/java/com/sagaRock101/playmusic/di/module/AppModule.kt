package com.sagaRock101.playmusic.di.module

import android.app.Application
import com.sagaRock101.playmusic.repo.MediaPlayerImpl
import com.sagaRock101.playmusic.repo.SongsRepoImpl
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun provideRepositoryImpl(application: Application) = SongsRepoImpl(application)

    @Provides
    fun provideMediaPlayerRepoImpl(application: Application) = MediaPlayerImpl(application)
}