package com.juniori.puzzle.domain.usecase

import com.juniori.puzzle.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, long: Double) =
        weatherRepository.getWeatherInfo(lat, long)

}

