package com.juniori.puzzle.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.R
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.position.PositionResponse
import com.juniori.puzzle.domain.customtype.WeatherException
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.usecase.*
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
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _weatherState = MutableStateFlow<APIResponse<Unit>>(APIResponse.Loading)
    val weatherState: StateFlow<APIResponse<Unit>> = _weatherState

    private val _welcomeText = MutableStateFlow("")
    val welcomeText: StateFlow<String> = _welcomeText

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _currentAddress = MutableStateFlow("")
    val currentAddress: StateFlow<String> = _currentAddress

    private val _weatherSubList = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherSubList: StateFlow<List<WeatherEntity>> = _weatherSubList

    private val _weatherMainInfo =
        MutableStateFlow(WeatherEntity(Date(), 0, 0, 0, 0, "", ""))
    val weatherMainInfo: StateFlow<WeatherEntity> = _weatherMainInfo

    fun setDisplayName() {
        val userInfo = getUserInfoUseCase()
        if (userInfo is APIResponse.Success) {
            _displayName.value = userInfo.result.nickname
        } else {
            _displayName.value = ""
        }
    }

    fun setWelcomeText(welcomeTextArray: Array<String>) {
        _welcomeText.value = showWelcomeTextUseCase(welcomeTextArray)
    }

    fun getNewWeatherData(position: PositionResponse) {
        viewModelScope.launch {
            _weatherState.value = APIResponse.Loading

            when (val weatherInfo = getCurrentWeatherUseCase(position)) {
                is APIResponse.Success<Pair<WeatherEntity, List<WeatherEntity>>> -> {
                    _weatherMainInfo.value = weatherInfo.result.first
                    _weatherSubList.value = weatherInfo.result.second
                    _weatherState.value = APIResponse.Success(Unit)
                }
                is APIResponse.Failure -> {
                    _weatherState.value = weatherInfo
                }
                is APIResponse.Loading -> {
                    _weatherState.value = APIResponse.Loading
                }
            }
        }
    }

    fun setWeatherStateSuccess() {
        _weatherState.value = APIResponse.Success(Unit)
    }

    fun setWeatherStateError(exception: java.lang.Exception) {
        _weatherState.value = APIResponse.Failure(exception)
    }

    fun setWeatherStateLoading() {
        _weatherState.value = APIResponse.Loading
    }

    fun setCurrentAddress(address: String) {
        _currentAddress.value = address
    }
}
