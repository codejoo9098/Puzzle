package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.WeatherRepository
import javax.inject.Inject

class UnregisterLocationListenerUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke() = repository.unregisterLocationListener()
}