package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.LocationStateRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val locationStateRepository: LocationStateRepository
) {
    suspend operator fun invoke(lat: Double, long: Double) =
        locationStateRepository.getWeatherInfo(lat, long)

}

