package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.LocationStateRepository
import javax.inject.Inject

class UnregisterLocationListenerUseCase @Inject constructor(
    private val repository: LocationStateRepository
) {
    operator fun invoke() = repository.unregisterLocationListener()
}