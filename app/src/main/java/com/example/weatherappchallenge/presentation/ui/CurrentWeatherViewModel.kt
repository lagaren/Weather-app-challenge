package com.example.weatherappchallenge.presentation.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherappchallenge.model.CurrentWeatherRepository
import com.example.weatherappchallenge.model.data.CurrentWeather
import com.example.weatherappchallenge.util.DataStoreManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentWeatherViewModel constructor(
    private val currentWeatherRepository: CurrentWeatherRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _currentWeather = MutableStateFlow(CurrentWeather())
    val currentWeather: StateFlow<CurrentWeather> get() = _currentWeather

    private val errorMessage = MutableLiveData<String>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    var lastCitySearched = ""

    //Calls out to the Openweather API via latitude and longitude
    fun getCurrentWeatherByCoord(lat: String, long: String) {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response =
                currentWeatherRepository.getCurrentWeatherByCoord(
                    lat = lat,
                    long = long
                )

            withContext(Dispatchers.Main) {
                //If network call is successful push weather data to the flow. Otherwise handle error.
                if (response.isSuccessful && response.body() != null) {
                    _currentWeather.value = response.body() as CurrentWeather
                    lastCitySearched = (response.body() as CurrentWeather).name ?: ""
                } else {
                    onError(response.message())
                }
            }
            //Save this search to populate search field later
            dataStoreManager.saveLastSearch(lastCitySearched)
        }
    }

    //Calls out to the Openweather API via city name
    fun getCurrentWeatherByCity(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                currentWeatherRepository.getCurrentWeatherByCity(
                    city = city
                )

            withContext(Dispatchers.Main) {
                //If network call is successful push weather data to the flow. Otherwise handle error.
                if (response.isSuccessful) {
                    _currentWeather.value = response.body() as CurrentWeather
                    lastCitySearched = (response.body() as CurrentWeather).name ?: ""
                } else {
                    onError(response.message())
                }
            }
            //Save this search to populate search field later
            dataStoreManager.saveLastSearch(lastCitySearched)
        }
    }

    fun getLastCitySearched() {
        CoroutineScope(Dispatchers.IO).launch {
            lastCitySearched = dataStoreManager.getLastSearch.first()
        }

    }

    private fun onError(message: String) {
        errorMessage.value = message
    }



}
