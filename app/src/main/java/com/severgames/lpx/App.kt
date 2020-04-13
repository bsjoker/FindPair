package com.severgames.lpx

import android.app.Application
import android.util.Log

class App: Application() {
    override fun onCreate() {
        super.onCreate()

        instance = this
    }

    companion object {
        lateinit var instance: App
            private set
    }
}