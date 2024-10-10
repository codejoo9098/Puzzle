package com.juniori.puzzle.domain.repository

import android.location.Address
import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.domain.entity.WeatherEntity

interface LocationStateRepository {
    fun registerLocationListener(listener: LocationListenerCompat):Boolean
    fun unregisterLocationListener()
    fun getAddressInfo(lat: Double, long: Double): List<Address>
    suspend fun getWeatherInfo(lat: Double, long: Double): APIResponse<List<WeatherEntity>>
}