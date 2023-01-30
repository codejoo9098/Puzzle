package com.juniori.puzzle.di

import com.google.firebase.auth.FirebaseAuth
import com.juniori.puzzle.domain.repository.AuthRepository
import com.juniori.puzzle.data.repositoryimpl.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AuthModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

}