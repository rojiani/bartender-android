@file:JvmName("MockDataUtils")

package com.nrojiani.bartender.data.test.utils

import com.nrojiani.bartender.di.NetworkModule
import com.squareup.moshi.JsonAdapter
import timber.log.Timber
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

val NETWORK_MOCKS_PATH: Path = Paths.get("src/test/resources/network")

internal fun readMockJson(
    mocksDir: Path = NETWORK_MOCKS_PATH,
    filename: String
): String = readMockJson(mocksDir.resolve(filename))

fun readMockJson(path: Path): String = path
    .readText()
    .also { Timber.d("json: \n$it") }

inline fun <reified T> fromMockJson(
    mocksDir: Path = NETWORK_MOCKS_PATH,
    mocksRelativePath: String
): T {
    val moshi = NetworkModule.provideMoshi()
    val adapter: JsonAdapter<T> = moshi.adapter(T::class.java)
    val mockJson: String = readMockJson(mocksDir.resolve(mocksRelativePath))
    val result: T? = adapter.fromJson(mockJson)
    return requireNotNull(result)
}

inline fun <reified T> fromMockJson(
    mocksJsonPath: Path,
): T {
    val moshi = NetworkModule.provideMoshi()
    val adapter: JsonAdapter<T> = moshi.adapter(T::class.java)
    val mockJson: String = readMockJson(mocksJsonPath)
    val result: T? = adapter.fromJson(mockJson)
    return requireNotNull(result)
}
