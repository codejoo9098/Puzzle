package com.juniori.puzzle.app.di

import com.juniori.puzzle.data.datasource.position.PositionDataSource
import com.juniori.puzzle.data.datasource.position.PositionDataSourceImpl
import com.juniori.puzzle.data.repositoryimpl.LocationStateRepositoryImpl
import com.juniori.puzzle.domain.repository.LocationStateRepository
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
    fun providesLocationRepository(impl: LocationStateRepositoryImpl): LocationStateRepository = impl

    @Singleton
    @Provides
    fun providesLocationDataSource(impl: PositionDataSourceImpl): PositionDataSource = impl

}