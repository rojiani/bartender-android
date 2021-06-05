@file:JvmName("MockDataUtils")

package com.nrojiani.bartender.data.test.utils

import timber.log.Timber
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.readText

internal val NETWORK_MOCKS_PATH: Path = Paths.get("src/test/resources/network")
private val NETWORK_SEARCH_BY_NAME_MOCKS_PATH: Path = Paths.get("$NETWORK_MOCKS_PATH/search/name")

internal fun readMockJson(
    mocksDir: Path = NETWORK_MOCKS_PATH,
    filename: String
): String = readMockJson(mocksDir.resolve(filename))

internal fun readMockJson(path: Path): String = path
    .readText()
    .also { Timber.d("json: \n$it") }

internal fun readMockSearchByDrinkNameJson(mockFilename: String): String =
    NETWORK_SEARCH_BY_NAME_MOCKS_PATH.resolve(mockFilename)
        .readText()
        .also { Timber.d("json: \n$it") }
