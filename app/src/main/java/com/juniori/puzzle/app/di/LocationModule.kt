package com.juniori.puzzle.app.di

import com.juniori.puzzle.data.repositoryimpl.WeatherRepositoryImpl
import com.juniori.puzzle.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    @Singleton
    @Provides
    fun providesLocationRepository(impl: WeatherRepositoryImpl): WeatherRepository = impl
}