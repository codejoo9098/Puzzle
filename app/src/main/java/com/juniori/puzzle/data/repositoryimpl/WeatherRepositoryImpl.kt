package com.juniori.puzzle.data.repositoryimpl

import com.juniori.puzzle.data.datasource.weather.WeatherDataSource
import com.juniori.puzzle.data.datasource.weather.WeatherResponse
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherDataSource: WeatherDataSource,
) : WeatherRepository {
    override suspend fun getWeatherInfo(lat: Double, long: Double): TempAPIResponse<WeatherResponse> {
        return weatherDataSource.getWeather(lat, long)
    }
}