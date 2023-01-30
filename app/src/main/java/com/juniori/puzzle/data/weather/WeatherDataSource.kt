package com.juniori.puzzle.data.weather

import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.WeatherEntity

interface WeatherDataSource {
    suspend fun getWeather(lat: Double, lon: Double): APIResponse<List<WeatherEntity>>
}