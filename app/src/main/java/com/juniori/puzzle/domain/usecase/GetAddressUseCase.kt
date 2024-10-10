package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.WeatherRepository
import javax.inject.Inject

class GetAddressUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(lat: Double, long: Double) = repository.getAddressInfo(lat, long)
}