@file:JvmName("LiveDataUtils")
package com.nrojiani.bartender.test.utils.livedata

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * [Blog Post](https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04)
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(observer)

    try {
        afterObserve.invoke()

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }
    } finally {
        this.removeObserver(observer)
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

/**
 * Captures all LiveData values during observation in a List.
 * Modified version of the `observeForTesting` extension function mentioned in this
 * [Blog Post](https://medium.com/androiddevelopers/unit-testing-livedata-and-other-common-observability-problems-bb477262eb04),
 * which can be found in [android/architecture-components-samples: LiveDataTestUtil.kt](https://github.com/android/architecture-components-samples/blob/master/LiveDataSample/app/src/test/java/com/android/example/livedatabuilder/util/LiveDataTestUtil.kt)
 */
fun <T> LiveData<T>.recordLiveDataHistory(block: () -> Unit): List<T> {
    val liveDataValues = ArrayList<T>()
    val observer = Observer(liveDataValues::add)
    try {
        this.observeForever(observer)
        block()
    } finally {
        this.removeObserver(observer)
    }
    return liveDataValues
}
