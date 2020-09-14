package com.sagaRock101.playmusic

import android.app.Application
import com.sagaRock101.playmusic.di.AppComponent
import com.sagaRock101.playmusic.di.DaggerAppComponent
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}