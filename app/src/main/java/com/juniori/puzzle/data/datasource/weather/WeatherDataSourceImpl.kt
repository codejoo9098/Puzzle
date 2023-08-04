package com.juniori.puzzle.data.datasource.weather

import com.juniori.puzzle.app.util.WEATHER_SERVICE_KEY
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import java.io.IOException
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

    override suspend fun getWeather(lat: Double, lon: Double): TempAPIResponse<WeatherResponse> {
        return try {
            val response = service.getWeather(lat, lon, WEATHER_SERVICE_KEY, language)
            response.body()?.let { if (response.code() >= 400) TempAPIResponse.Failure(APIErrorType.SERVER_ERROR) else TempAPIResponse.Success(it) } ?: TempAPIResponse.Failure(APIErrorType.NO_CONTENT)
        }
        catch (e: IOException) {
            TempAPIResponse.Failure(APIErrorType.NOT_CONNECTED)
        }
    }
}