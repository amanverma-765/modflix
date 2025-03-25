package com.ark.modflix

import android.app.Application

class ModFlixApp: Application() {
    override fun onCreate() {
        super.onCreate()
//        startKoin {
//            androidContext(this@ModFlixApp)
//            modules(sharedModule, platformModule)
//        }
    }
}