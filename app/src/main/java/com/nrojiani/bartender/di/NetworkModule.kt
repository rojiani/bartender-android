package com.nrojiani.bartender.di

import android.content.Context
import com.nrojiani.bartender.BuildConfig
import com.nrojiani.bartender.data.remote.datasource.DrinksRemoteDataSource
import com.nrojiani.bartender.data.remote.datasource.IDrinksRemoteDataSource
import com.nrojiani.bartender.data.remote.webservice.CocktailsService
import com.nrojiani.bartender.data.remote.webservice.TheCocktailDbApi
import com.nrojiani.bartender.utils.connectivity.NetworkStatusMonitor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideNetworkStatusMonitor(@ApplicationContext appContext: Context): NetworkStatusMonitor =
        NetworkStatusMonitor(appContext)

    @Singleton
    @Provides
    fun provideDrinksRemoteDataSource(cocktailsService: CocktailsService): IDrinksRemoteDataSource =
        DrinksRemoteDataSource(cocktailsService)

    @Singleton
    @Provides
    fun provideCocktailsWebService(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): CocktailsService {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(TheCocktailDbApi.COCKTAILDB_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()

        return retrofit.create(CocktailsService::class.java)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(
            HttpLoggingInterceptor { message ->
                Timber.tag("OkHttp").d(message)
            }.apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.HEADERS
                } else {
                    HttpLoggingInterceptor.Level.BASIC
                }
            }
        )
        .build()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)
}
