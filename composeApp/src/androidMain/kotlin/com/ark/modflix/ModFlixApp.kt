package com.ark.modflix

import android.app.Application
import com.ark.modflix.koin.platformModule
import com.ark.modflix.koin.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ModFlixApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ModFlixApp)
            modules(sharedModule, platformModule)
        }
    }
}