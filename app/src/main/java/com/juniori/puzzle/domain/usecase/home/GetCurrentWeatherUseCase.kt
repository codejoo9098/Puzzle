package com.juniori.puzzle.domain.usecase.home

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.converter.toWeatherEntity
import com.juniori.puzzle.data.datasource.position.PositionResponse
import com.juniori.puzzle.domain.customtype.WeatherException
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.repository.WeatherRepository
import java.io.IOException
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(loc: PositionResponse): APIResponse<Pair<WeatherEntity, List<WeatherEntity>>> {
        if (loc.lat < -90 || loc.lat > 90 || loc.lon < -180 || loc.lon > 180) return APIResponse.Failure(WeatherException.LocationErrorException)

        try {
            val response = weatherRepository.getWeatherInfo(loc.lat, loc.lon)?.toWeatherEntity() ?: return APIResponse.Failure(WeatherException.WeatherServerErrorException)

            return if (response.size >= 3) {
                val mainWeatherInfo = response[1]
                val subWeatherList = response.subList(2, response.size)

                APIResponse.Success(Pair(mainWeatherInfo, subWeatherList))
            } else {
                APIResponse.Failure(WeatherException.WeatherServerErrorException)
            }
        }
        catch (e: IOException) {
            return APIResponse.Failure(WeatherException.NetworkErrorException)
        }
    }
}