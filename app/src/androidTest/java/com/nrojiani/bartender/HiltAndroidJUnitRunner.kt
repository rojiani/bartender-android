package com.nrojiani.bartender

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

/**
 * Custom test runner for using Hilt in instrumented tests.
 * [Reference](https://developer.android.com/training/dependency-injection/hilt-testing#instrumented-tests)
 */
class HiltAndroidJUnitRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
