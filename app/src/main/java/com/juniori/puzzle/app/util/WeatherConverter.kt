package com.juniori.puzzle.app.util

import com.juniori.puzzle.data.datasource.weather.WeatherResponse
import com.juniori.puzzle.domain.entity.WeatherEntity
import kotlin.math.roundToInt

fun WeatherResponse.toItem(): List<WeatherEntity> {
    return list.map {
        WeatherEntity(
            date = it.dtTxt.toDate(),
            temp = it.main.temp.roundToInt(),
            feelsLike = it.main.feelsLike.roundToInt(),
            minTemp = it.main.tempMin.roundToInt(),
            maxTemp = it.main.tempMax.roundToInt(),
            description = it.weather[0].description,
            icon = "$WEATHER_ICON_URL/${it.weather[0].icon}@2x.png"
        )
    }
}