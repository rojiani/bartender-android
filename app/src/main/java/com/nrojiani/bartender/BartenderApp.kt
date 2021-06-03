package com.nrojiani.bartender

import android.app.Application
import com.nrojiani.bartender.utils.logging.CustomDebugTree
import timber.log.Timber

class BartenderApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(CustomDebugTree())
        }
    }
}
