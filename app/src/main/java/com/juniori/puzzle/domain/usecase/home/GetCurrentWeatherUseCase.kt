package com.juniori.puzzle.domain.usecase.home

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.converter.toWeatherEntity
import com.juniori.puzzle.data.datasource.position.PositionResponse
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.customtype.WeatherException
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.repository.WeatherRepository
import java.io.IOException
import javax.inject.Inject

class GetCurrentWeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(loc: PositionResponse): TempAPIResponse<Pair<WeatherEntity, List<WeatherEntity>>> {
        return when(val response = weatherRepository.getWeatherInfo(loc.lat, loc.lon)) {
            is TempAPIResponse.Success -> {
                val weatherList = response.data.toWeatherEntity()

                if (weatherList.size >= 3) {
                    val mainWeatherInfo = weatherList[1]
                    val subWeatherList = weatherList.subList(2, weatherList.size)

                    TempAPIResponse.Success(Pair(mainWeatherInfo, subWeatherList))
                }
                else {
                    TempAPIResponse.Failure(APIErrorType.SERVER_ERROR)
                }
            }
            is TempAPIResponse.Failure -> {
                TempAPIResponse.Failure(response.errorType)
            }
        }
    }
}