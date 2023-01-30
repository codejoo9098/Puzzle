package com.juniori.puzzle.data.repositoryimpl

import android.location.Address
import androidx.core.location.LocationListenerCompat
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.position.PositionDataSource
import com.juniori.puzzle.data.datasource.weather.WeatherDataSource
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.repository.LocationStateRepository
import javax.inject.Inject

class LocationStateRepositoryImpl @Inject constructor(
    private val positionDataSource: PositionDataSource,
    private val weatherDataSource: WeatherDataSource,
) : LocationStateRepository {
    override fun registerLocationListener(listener: LocationListenerCompat): Boolean {
        return positionDataSource.registerLocationListener(listener)
    }

    override fun unregisterLocationListener() {
        positionDataSource.unregisterLocationListener()
    }

    override fun getAddressInfo(lat: Double, long: Double): List<Address> {
        return positionDataSource.getCurrentAddress(lat, long)
    }

    override suspend fun getWeatherInfo(lat: Double, long: Double): APIResponse<List<WeatherEntity>> {
        return weatherDataSource.getWeather(lat, long)
    }
}