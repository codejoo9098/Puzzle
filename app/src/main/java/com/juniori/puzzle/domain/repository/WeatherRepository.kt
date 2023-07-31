package com.juniori.puzzle.domain.repository

import android.location.Address
import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.weather.WeatherResponse
import com.juniori.puzzle.domain.entity.WeatherEntity

interface WeatherRepository {
    suspend fun getWeatherInfo(lat: Double, long: Double): WeatherResponse?
}