@file:JvmName("MockData")

package com.nrojiani.bartender.test.utils.mocks

import com.nrojiani.bartender.data.domain.DrinkRef
import com.nrojiani.bartender.di.NetworkModule
import com.squareup.moshi.JsonAdapter
import timber.log.Timber
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

val NETWORK_MOCKS_PATH: Path = Paths.get("src/sharedTest/resources/network")

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

object DrinkRefs {
    val GIN_SOUR = DrinkRef(
        id = "11417",
        drinkName = "Gin Sour",
        imageUrl = "https://www.thecocktaildb.com/images/media/drink/noxp7e1606769224.jpg"
    )

    val BLUEBERRY_MOJITO_DRINK_REF = DrinkRef(
        id = "178336",
        imageUrl = "https://www.thecocktaildb.com/images/media/drink/07iep51598719977.jpg",
        drinkName = "Blueberry Mojito"
    )
}
