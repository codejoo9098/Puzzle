package com.juniori.puzzle.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.juniori.puzzle.R
import com.juniori.puzzle.app.util.extensions.toAddressString
import com.juniori.puzzle.ui.adapter.WeatherRecyclerViewAdapter
import com.juniori.puzzle.data.APIResponse
import com.juniori.puzzle.data.datasource.position.PositionResponse
import com.juniori.puzzle.databinding.FragmentHomeBinding
import com.juniori.puzzle.domain.customtype.WeatherException
import com.juniori.puzzle.ui.sensor.SensorActivity
import com.juniori.puzzle.ui.common_ui.StateManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    @Inject
    lateinit var stateManager: StateManager

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: WeatherRecyclerViewAdapter
    private var currentPosition = PositionResponse(0.0, 0.0)

    private val applicationContext: Context by lazy { requireActivity().applicationContext }

    private val locationListener = object : LocationListenerCompat {
        override fun onLocationChanged(loc: Location) {
            currentPosition = PositionResponse(loc.latitude, loc.longitude)
            setCurrentAddress()
            homeViewModel.getNewWeatherData(currentPosition)
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            homeViewModel.setWeatherStateError(WeatherException.LocationServiceOffException)
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            homeViewModel.setWeatherStateSuccess()
        }
    }


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermitted ->
        homeViewModel.setWeatherStateLoading()

        if (isPermitted.not()) {
            homeViewModel.setWeatherStateError(WeatherException.PermissionOffException)
        } else {
            if (registerLocationListener()) homeViewModel.setWeatherStateSuccess()
            else homeViewModel.setWeatherStateError(WeatherException.LocationServiceOffException)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            vm = homeViewModel
        }
        stateManager.createLoadingDialog(container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = WeatherRecyclerViewAdapter()
        checkPermission()

        binding.run {
            weatherRefreshBtn.setOnClickListener {
                setCurrentAddress()
                refreshWeatherData()
            }

            refreshBtn.setOnClickListener {
                setCurrentAddress()
                refreshWeatherData()
            }

            golfBtnBackground.setOnClickListener {
                val intent = Intent(requireActivity(), SensorActivity::class.java)
                startActivity(intent)
            }

            weatherDetailRecyclerView.adapter = adapter
        }

        lifecycleScope.launchWhenStarted {
            homeViewModel.weatherState.collect { resource ->
                when (resource) {
                    is APIResponse.Success -> {
                        stateManager.dismissLoadingDialog()
                        showWeather()
                    }
                    is APIResponse.Failure -> {
                        stateManager.dismissLoadingDialog()

                        when (resource.exception) {
                            is WeatherException.LocationErrorException -> hideWeather(getString(R.string.location_fail))
                            is WeatherException.WeatherServerErrorException -> hideWeather(getString(R.string.weather_server_error))
                            is WeatherException.NetworkErrorException -> hideWeather(getString(R.string.network_fail))
                            is WeatherException.PermissionOffException -> hideWeather(getString(R.string.location_permission))
                            is WeatherException.LocationServiceOffException -> hideWeather(getString(R.string.location_service_off))
                            else -> hideWeather(getString(R.string.unknown_error))
                        }
                    }
                    is APIResponse.Loading -> {
                        stateManager.showLoadingDialog()
                    }
                }
            }
        }

        homeViewModel.run {
            val welcomeTextArray = resources.getStringArray(R.array.welcome_text)
            setWelcomeText(welcomeTextArray)
            setDisplayName()
        }
    }

    override fun onPause() {
        super.onPause()
        stateManager.dismissLoadingDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unregisterLocationListener()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationListener(): Boolean {
        val locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_MIN_TIME_INTERVAL,
                LOCATION_MIN_DISTANCE_INTERVAL,
                locationListener
            )

            true
        }
        else {
            false
        }
    }

    private fun unregisterLocationListener() {
        val locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.removeUpdates(locationListener)
    }

    private fun setCurrentAddress() {
        val geoCoder = Geocoder(applicationContext)

        try {
            val address = geoCoder.getFromLocation(currentPosition.lat, currentPosition.lon, ADDRESS_MAX_RESULT)[0].toAddressString()
            homeViewModel.setCurrentAddress(address)
        } catch (e: Exception) {
            homeViewModel.setCurrentAddress(getString(R.string.location_fail))
        }
    }

    private fun refreshWeatherData() {
        val locationManager: LocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            homeViewModel.setWeatherStateError(WeatherException.PermissionOffException)
            return
        }
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            homeViewModel.setWeatherStateError(WeatherException.LocationServiceOffException)
            return
        }
        else if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            homeViewModel.setWeatherStateError(WeatherException.NetworkErrorException)
            return
        }

        homeViewModel.getNewWeatherData(currentPosition)
    }

    private fun checkPermission() = locationPermissionRequest.launch(Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun showWeather() {
        binding.weatherLayout.isVisible = true
        binding.weatherFailLayout.isVisible = false
    }

    private fun hideWeather(text: String) {
        binding.weatherLayout.isVisible = false
        binding.weatherFailLayout.isVisible = true
        binding.errorReasonText.text = text
    }

    companion object {
        private const val LOCATION_MIN_TIME_INTERVAL = 3000L
        private const val LOCATION_MIN_DISTANCE_INTERVAL = 30f
        private const val ADDRESS_MAX_RESULT = 1
    }
}

