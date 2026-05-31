package com.vedantraut.herohub

import android.app.Application
import com.vedantraut.herohub.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class HeroHubApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@HeroHubApp)
            modules(appModule)
        }
    }
}