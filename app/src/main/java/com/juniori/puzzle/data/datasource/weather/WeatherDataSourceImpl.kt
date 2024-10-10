package com.juniori.puzzle.data.datasource.weather

import com.juniori.puzzle.app.util.WEATHER_SERVICE_KEY
import java.util.*
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val service: WeatherService
) : WeatherDataSource {

    private val language = when (Locale.getDefault().language) {
        "ko" -> {
            "kr"
        }
        "en" -> {
            "en"
        }
        else -> {
            "ko"
        }
    }

    override suspend fun getWeather(lat: Double, lon: Double): WeatherResponse? {
        val response = service.getWeather(lat, lon, WEATHER_SERVICE_KEY, language)
        return if (response.code() >= 400) null
        else response.body()
    }
}