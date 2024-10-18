package com.juniori.puzzle.domain.usecase.home

import com.juniori.puzzle.data.datasource.weather.WeatherCityResponse
import com.juniori.puzzle.data.datasource.weather.WeatherCloudResponse
import com.juniori.puzzle.data.datasource.weather.WeatherCoordResponse
import com.juniori.puzzle.data.datasource.weather.WeatherListResponse
import com.juniori.puzzle.data.datasource.weather.WeatherMainResponse
import com.juniori.puzzle.data.datasource.weather.WeatherRainResponse
import com.juniori.puzzle.data.datasource.weather.WeatherResponse
import com.juniori.puzzle.data.datasource.weather.WeatherSysResponse
import com.juniori.puzzle.data.datasource.weather.WeatherWeatherResponse
import com.juniori.puzzle.data.datasource.weather.WeatherWindResponse

val mockWeatherResponse = WeatherResponse(
    cod="200",
    message="0",
    cnt=11,
    list=listOf(
        WeatherListResponse(
            dt=1729090800,
            main= WeatherMainResponse(temp=25.45F, feelsLike=26.17F, tempMin=25.45F, tempMax=25.45F, pressure=1012, seaLevel=1012, groundLevel=1012, humidity=81, tempKf=0.0F),
            weather=listOf(WeatherWeatherResponse(id=801, main="Clouds", description="few clouds", icon="02d")),
            cloud=WeatherCloudResponse(0),
            wind= WeatherWindResponse(speed=6.96F, deg=175, gust=6.92F),
            visibility=10000,
            pop=0.0F,
            rain= WeatherRainResponse(0.0F),
            sys= WeatherSysResponse(pod="d"), dtTxt="2024-10-16 15:00:00"),
        WeatherListResponse(
            dt=1729101600,
            main=WeatherMainResponse(temp=25.48F, feelsLike=26.17F, tempMin=25.48F, tempMax=25.55F, pressure=1013, seaLevel=1013, groundLevel=1014, humidity=80, tempKf= -0.07F),
            weather=listOf(WeatherWeatherResponse(id=802, main="Clouds", description="scattered clouds", icon="03n")),
            cloud=WeatherCloudResponse(0),
            wind=WeatherWindResponse(speed=5.9F, deg=175, gust=5.62F),
            visibility=10000, pop=0.0F,
            rain=WeatherRainResponse(0.0F),
            sys=WeatherSysResponse(pod="n"),
            dtTxt="2024-10-16 18:00:00"),
        WeatherListResponse(
            dt=1729112400,
            main=WeatherMainResponse(temp=25.48F, feelsLike=26.15F, tempMin=25.48F, tempMax=25.49F, pressure=1014, seaLevel=1014, groundLevel=1015, humidity=79, tempKf=-0.01F),
            weather=listOf(WeatherWeatherResponse(id=802, main="Clouds", description="scattered clouds", icon="03n")),
            cloud=WeatherCloudResponse(0),
            wind=WeatherWindResponse(speed=5.35F, deg=173, gust=5.01F),
            visibility=10000,
            pop=0.0F,
            rain=WeatherRainResponse(0.0F),
            sys=WeatherSysResponse(pod="n"),
            dtTxt="2024-10-16 21:00:00"),
        WeatherListResponse(
            dt=1729123200,
            main=WeatherMainResponse(temp=25.27F, feelsLike=25.89F, tempMin=25.27F, tempMax=25.27F, pressure=1014, seaLevel=1014, groundLevel=1014, humidity=78, tempKf=0.0F),
            weather=listOf(WeatherWeatherResponse(id=803, main="Clouds", description="broken clouds", icon="04n")),
            cloud=WeatherCloudResponse(0),
            wind=WeatherWindResponse(speed=5.08F, deg=185, gust=4.84F),
            visibility=10000, pop=0.0F,
            rain=WeatherRainResponse(0.0F),
            sys=WeatherSysResponse(pod="n"), dtTxt="2024-10-17 00:00:00"),
        WeatherListResponse(
            dt=1729134000,
            main=WeatherMainResponse(temp=24.92F, feelsLike=25.53F, tempMin=24.92F, tempMax=24.92F, pressure=1013, seaLevel=1013, groundLevel=1013, humidity=79, tempKf=0.0F),
            weather=listOf(WeatherWeatherResponse(id=804, main="Clouds", description="overcast clouds", icon="04n")),
            cloud=WeatherCloudResponse(0),
            wind=WeatherWindResponse(speed=5.22F, deg=188, gust=5.01F),
            visibility=10000,
            pop=0.0F,
            rain=WeatherRainResponse(0.0F), sys=WeatherSysResponse(pod="n"), dtTxt="2024-10-17 03:00:00")),
    city = WeatherCityResponse(id=6295630, name="Globe", coord=WeatherCoordResponse(lat=0.0F, lon=0.0F), country="Korea", population=2147483647, timezone=0, sunrise=1729057317, sunset=1729100921)
)
