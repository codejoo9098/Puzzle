package com.juniori.puzzle.domain.usecase.home

import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.repository.WeatherRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GetCurrentWeatherUseCaseTest {
    lateinit var weatherRepository: WeatherRepository
    lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase

    @Before
    fun setUp() {
        weatherRepository = Mockito.mock(WeatherRepository::class.java)
        getCurrentWeatherUseCase = GetCurrentWeatherUseCase(weatherRepository)
    }

    @Test
    fun normalWeatherTest(): Unit = runBlocking {
        Mockito.`when`(weatherRepository.getWeatherInfo(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(TempAPIResponse.Success(mockWeatherResponse))

        assertTrue(getCurrentWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION) is TempAPIResponse.Success)
    }

    @Test
    fun emptyWeatherTest(): Unit = runBlocking {
        val emptyWeatherResponse = mockWeatherResponse.copy(list = listOf())
        Mockito.`when`(weatherRepository.getWeatherInfo(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(TempAPIResponse.Success(emptyWeatherResponse))

        assertTrue(getCurrentWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION) is TempAPIResponse.Failure)
    }

    @Test
    fun failWeatherTest(): Unit = runBlocking {
        Mockito.`when`(weatherRepository.getWeatherInfo(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(TempAPIResponse.Failure(APIErrorType.SERVER_ERROR))

        assertTrue(getCurrentWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION) is TempAPIResponse.Failure)
    }

    @Test
    fun shortWeatherListTest(): Unit = runBlocking {
        val shortWeatherResponse = mockWeatherResponse.copy(list = mockWeatherResponse.list.subList(0, 2))
        Mockito.`when`(weatherRepository.getWeatherInfo(NORMAL_LOCATION, NORMAL_LOCATION)).thenReturn(TempAPIResponse.Success(shortWeatherResponse))

        assertTrue(getCurrentWeatherUseCase(NORMAL_LOCATION, NORMAL_LOCATION) is TempAPIResponse.Failure)
    }

    companion object {
        const val NORMAL_LOCATION = 10.0
    }
}