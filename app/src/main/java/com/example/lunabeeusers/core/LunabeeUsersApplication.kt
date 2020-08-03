package com.example.lunabeeusers.core

import android.app.Application
import timber.log.Timber

class LunabeeUsersApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}