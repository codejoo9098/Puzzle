package com.juniori.puzzle.domain.customtype

import java.lang.Exception

sealed class WeatherException: Exception() {
    object LocationErrorException: WeatherException()
    object WeatherServerErrorException: WeatherException()
    object PermissionOffException: WeatherException()
    object LocationServiceOffException: WeatherException()
    object NetworkErrorException: WeatherException()
}
