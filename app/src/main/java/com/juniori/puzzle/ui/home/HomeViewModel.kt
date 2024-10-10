package com.juniori.puzzle.ui.home

import androidx.core.location.LocationListenerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juniori.puzzle.R
import com.juniori.puzzle.app.util.toAddressString
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.position.PositionResponse
import com.juniori.puzzle.domain.entity.WeatherEntity
import com.juniori.puzzle.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAddressUseCase: GetAddressUseCase,
    private val registerLocationListenerUseCase: RegisterLocationListenerUseCase,
    private val unregisterLocationListenerUseCase: UnregisterLocationListenerUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<APIResponse<List<WeatherEntity>>>(APIResponse.Loading)
    val uiState: StateFlow<APIResponse<List<WeatherEntity>>> = _uiState

    private val _welcomeText = MutableStateFlow("")
    val welcomeText: StateFlow<String> = _welcomeText

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _currentAddress = MutableStateFlow("")
    val currentAddress: StateFlow<String> = _currentAddress

    private val _weatherList = MutableStateFlow<List<WeatherEntity>>(emptyList())
    val weatherList: StateFlow<List<WeatherEntity>> = _weatherList

    private val _weatherMainList =
        MutableStateFlow(WeatherEntity(Date(), 0, 0, 0, 0, "", ""))
    val weatherMainList: StateFlow<WeatherEntity> = _weatherMainList

    private val _weatherFailTextId = MutableLiveData(R.string.location_empty)
    val weatherFailTextId: LiveData<Int> = _weatherFailTextId

    private val _lastLocationInfo =
        MutableStateFlow(PositionResponse(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY))

    private var locationTimerJob: Job? = null

    fun setUiState(state: APIResponse<List<WeatherEntity>>) {
        _uiState.value = state
    }

    fun setDisplayName() {
        val userInfo = getUserInfoUseCase()
        if (userInfo is APIResponse.Success) {
            _displayName.value = userInfo.result.nickname
        } else {
            _displayName.value = ""
        }
    }

    fun setWelcomeText(text: String) {
        _welcomeText.value = text
    }

    private fun setWeatherFailTextId(id: Int) {
        _weatherFailTextId.value = id
    }

    fun setWeatherInfoText(text: String) {
        _uiState.value = APIResponse.Failure(Exception(text))
    }

    private fun setCurrentAddress(lat: Double, long: Double) {
        val address = getAddressUseCase(lat, long)
        if (address.isNotEmpty()) {
            _currentAddress.value = address[0].toAddressString()
        }
    }

    fun registerListener(listener: LocationListenerCompat) {
        val result = registerLocationListenerUseCase(listener)
        if (result.not()) {
            setWeatherFailTextId(R.string.location_service_off)
        } else {
            locationTimerJob = CoroutineScope(Dispatchers.IO).launch {
                delay(3000)
                withContext(Dispatchers.Main) {
                    showWeather()
                }
            }
        }
    }

    fun unregisterListener() {
        unregisterLocationListenerUseCase()
    }

    fun cancelTimer() {
        locationTimerJob?.cancel()
    }

    fun getWeather(loc: PositionResponse) {
        _lastLocationInfo.value = loc
        viewModelScope.launch {
            when (val result = getWeatherUseCase(loc.lat, loc.lon)) {
                is APIResponse.Success<List<WeatherEntity>> -> {
                    val list = result.result
                    _weatherMainList.value = list[1]
                    _weatherList.value = list.subList(2, list.size)
                    setCurrentAddress(loc.lat, loc.lon)
                    _uiState.value = APIResponse.Success(list)
                }
                is APIResponse.Failure -> {
                    setWeatherFailTextId(R.string.network_fail)
                }
                is APIResponse.Loading -> _uiState.value = APIResponse.Loading
            }
        }
    }

    fun showWeather() {
        if (_weatherList.value.size < 3) {
            setWeatherFailTextId(R.string.location_empty)
        } else {
            _uiState.value = APIResponse.Success(_weatherList.value)
        }
    }

}
