package com.nrojiani.bartender.utils.logging

import timber.log.Timber

/**
 * A [Timber] DebugTree with the name of the thread.
 */
class CustomDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${super.createStackElementTag(element)} [${Thread.currentThread().name}]"
    }
}
