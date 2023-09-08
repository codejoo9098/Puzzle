package com.juniori.puzzle.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.domain.APIErrorType
import com.juniori.puzzle.domain.TempAPIResponse
import com.juniori.puzzle.domain.entity.UserInfoEntity
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.usecase.common.GetUserInfoUseCase
import com.juniori.puzzle.domain.usecase.home.GetCurrentWeatherUseCase
import com.juniori.puzzle.domain.usecase.home.ShowWelcomeTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val showWelcomeTextUseCase: ShowWelcomeTextUseCase,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _currentUserInfo = getUserInfoUseCase()
    val currentUserInfo: StateFlow<TempAPIResponse<UserInfoEntity>> = _currentUserInfo

    private val _weatherState = MutableStateFlow(WeatherStatusType.LOADING)
    val weatherState: StateFlow<WeatherStatusType> = _weatherState

    private val _welcomeText = MutableStateFlow("")
    val welcomeText: StateFlow<String> = _welcomeText

    private val _currentAddress = MutableStateFlow("")
    val currentAddress: StateFlow<String> = _currentAddress

    private val _weatherSubList = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherSubList: StateFlow<List<WeatherEntity>> = _weatherSubList

    private val _weatherMainInfo =
        MutableStateFlow(WeatherEntity(Date(), 0, 0, 0, 0, "", ""))
    val weatherMainInfo: StateFlow<WeatherEntity> = _weatherMainInfo

    fun setWelcomeText(welcomeTextArray: Array<String>) {
        _welcomeText.value = showWelcomeTextUseCase(welcomeTextArray)
    }

    fun getNewWeatherData(position: PositionResponse) {
        viewModelScope.launch {
            setWeatherStateLoading()

            when (val response = getCurrentWeatherUseCase(position.lat, position.lon)) {
                is TempAPIResponse.Success<Pair<WeatherEntity, List<WeatherEntity>>> -> {
                    _weatherMainInfo.value = response.data.first
                    _weatherSubList.value = response.data.second
                    setWeatherStateSuccess()
                }
                is TempAPIResponse.Failure -> {
                    when(response.errorType) {
                        APIErrorType.NO_CONTENT -> setWeatherStateError(WeatherStatusType.SERVER_ERROR)
                        APIErrorType.SERVER_ERROR -> setWeatherStateError(WeatherStatusType.SERVER_ERROR)
                        APIErrorType.NOT_CONNECTED -> setWeatherStateError(WeatherStatusType.NETWORK_ERROR)
                    }
                }
            }
        }
    }

    private fun setWeatherStateSuccess() {
        _weatherState.value = WeatherStatusType.SUCCESS
    }

    fun setWeatherStateError(errorType: WeatherStatusType) {
        if (errorType == WeatherStatusType.SUCCESS || errorType == WeatherStatusType.LOADING) _weatherState.value = WeatherStatusType.UNKNOWN_ERROR
        else _weatherState.value = errorType
    }

    fun setWeatherStateLoading() {
        _weatherState.value = WeatherStatusType.LOADING
    }

    fun setCurrentAddress(address: String) {
        _currentAddress.value = address
    }
}
