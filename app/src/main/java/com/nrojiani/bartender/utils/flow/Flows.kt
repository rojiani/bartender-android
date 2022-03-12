@file:JvmName("Flows")

package com.nrojiani.bartender.utils.flow

import kotlinx.coroutines.flow.SharingStarted

@Suppress("MaxLineLength")
/**
 * See [Things to know about Flow's shareIn and stateIn operators](https://tinyurl.com/y8qkp3qa),
 * and [Migrating from LiveData to Kotlin's Flow](https://tinyurl.com/yahla8qq).
 *
 * When using [kotlinx.coroutines.flow.stateIn] or [kotlinx.coroutines.flow.shareIn] and
 * [SharingStarted.WhileSubscribed], this can be passed in for the
 * `stopTimeoutMillis` parameter. The default of `0` can be problematic on Android in some cases
 * when a view stops subscribing for a few milliseconds during configuration change.
 */
const val FLOW_STOP_TIMEOUT_MS: Long = 5000

/**
 * From Google's [Trackr](https://tinyurl.com/yc9zp6a6) sample app
 *
 * A [SharingStarted] meant to be used with a StateFlow to expose data to a view.
 *
 * When the view stops observing, upstream flows stay active for some time to allow the system to
 * come back from a short-lived configuration change (such as rotations). If the view stops
 * observing for longer, the cache is kept but the upstream flows are stopped. When the view comes
 * back, the latest value is replayed and the upstream flows are executed again. This is done to
 * save resources when the app is in the background but let users switch between apps quickly.
 */
val WhileViewSubscribed = SharingStarted.WhileSubscribed(FLOW_STOP_TIMEOUT_MS)
