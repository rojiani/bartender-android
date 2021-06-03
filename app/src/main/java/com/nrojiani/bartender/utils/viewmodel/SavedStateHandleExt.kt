@file:JvmName("SavedStateHandleExt")
package com.nrojiani.bartender.utils.viewmodel

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import java.io.Serializable

/**
 * This is a workaround to enable retrieving fragment args generated with the SafeArgs plugin
 * and passed at runtime in a ViewModel annotated with `@HiltViewModel`.
 * [Dagger issue #2287](https://github.com/google/dagger/issues/2287#issuecomment-830003347)
 *
 * It will no longer be necessary with the release of
 * [androidx 122](https://github.com/androidx/androidx/pull/122)
 */
inline fun <reified Args : NavArgs> SavedStateHandle.navArgs() = NavArgsLazy(Args::class) {
    val bundle = Bundle()
    keys().forEach {
        val value = get<Any>(it)
        if (value is Serializable) {
            bundle.putSerializable(it, value)
        } else if (value is Parcelable) {
            bundle.putParcelable(it, value)
        }
    }
    bundle
}
