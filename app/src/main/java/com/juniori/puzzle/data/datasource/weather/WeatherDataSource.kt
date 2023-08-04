package com.juniori.puzzle.data.datasource.weather

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.WeatherEntity

interface WeatherDataSource {
    suspend fun getWeather(lat: Double, lon: Double): TempAPIResponse<WeatherResponse>
}