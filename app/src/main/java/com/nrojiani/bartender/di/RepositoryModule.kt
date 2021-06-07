package com.nrojiani.bartender.di

import com.nrojiani.bartender.data.remote.datasource.IDrinksRemoteDataSource
import com.nrojiani.bartender.data.repository.DrinksRepository
import com.nrojiani.bartender.data.repository.IDrinksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDrinksRepository(remoteDataSource: IDrinksRemoteDataSource): IDrinksRepository =
        DrinksRepository(remoteDataSource)
}
