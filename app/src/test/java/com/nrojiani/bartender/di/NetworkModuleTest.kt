package com.nrojiani.bartender.di

import com.nrojiani.bartender.BuildConfig
import io.kotest.matchers.shouldBe
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test

class NetworkModuleTest {

    @Test
    fun provideOkHttpClient_logging_level_varies_by_buildconfig() {
        val okHttpClient = NetworkModule.provideOkHttpClient()
        val loggingInterceptor = okHttpClient
            .networkInterceptors()
            .filterIsInstance<HttpLoggingInterceptor>()
            .first()
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level.shouldBe(HttpLoggingInterceptor.Level.HEADERS)
        } else {
            loggingInterceptor.level.shouldBe(HttpLoggingInterceptor.Level.BASIC)
        }
    }
}
