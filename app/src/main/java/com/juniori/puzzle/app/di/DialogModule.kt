package com.juniori.puzzle.app.di

import android.content.Context
import com.juniori.puzzle.ui.common_ui.StateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object DialogModule {

    @Provides
    fun providesDialog(@ActivityContext context: Context) = StateManager(context)

}