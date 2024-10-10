package com.juniori.puzzle.data.datasource.position

import android.location.Address
import androidx.core.location.LocationListenerCompat

interface PositionDataSource {
    fun registerLocationListener(listener: LocationListenerCompat): Boolean
    fun unregisterLocationListener()
    fun getCurrentAddress(lat: Double, long: Double): List<Address>
}