@file:JvmName("Flows")

package com.nrojiani.bartender.utils.flow

import kotlinx.coroutines.flow.SharingStarted

@Suppress("MaxLineLength")
/**
 * See [Things to know about Flow's shareIn and stateIn operators](https://medium.com/androiddevelopers/things-to-know-about-flows-sharein-and-statein-operators-20e6ccb2bc74),
 * and [Migrating from LiveData to Kotlin's Flow](https://medium.com/androiddevelopers/migrating-from-livedata-to-kotlins-flow-379292f419fb).
 *
 * When using [kotlinx.coroutines.flow.stateIn] or [kotlinx.coroutines.flow.shareIn] and
 * [SharingStarted.WhileSubscribed], this can be passed in for the
 * `stopTimeoutMillis` parameter. The default of `0` can be problematic on Android in some cases
 * when a view stops subscribing for a few milliseconds during configuration change.
 */
const val FLOW_STOP_TIMEOUT_MS: Long = 5000
