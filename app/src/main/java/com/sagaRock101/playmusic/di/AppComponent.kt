package com.sagaRock101.playmusic.di

import android.app.Application
import android.content.Context
import com.sagaRock101.playmusic.di.module.AppModule
import com.sagaRock101.playmusic.ui.fragment.ListOfSongsFragment
import com.sagaRock101.playmusic.ui.fragment.ParentTabFragment
import com.sagaRock101.playmusic.ui.fragment.PlayerFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }

    fun inject(fragment: ListOfSongsFragment)
    fun inject(fragment: PlayerFragment)
}