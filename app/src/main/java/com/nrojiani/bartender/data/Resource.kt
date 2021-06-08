package com.nrojiani.bartender.data

/**
 * Represents a request for data along with its current status.
 * This is a slightly modified version of [Resource](https://developer.android.com/jetpack/guide#addendum)
 * from the Android app architecture guide.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Failure(val exception: Throwable) : Resource<Nothing>()
    object Loading : Resource<Nothing>() {
        override fun toString(): String = "Resource.Loading"
    }

    val dataOrNull: T? by lazy { (this as? Success<T>)?.data }

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success<T>

    val isFailure: Boolean
        get() = this is Failure
}
