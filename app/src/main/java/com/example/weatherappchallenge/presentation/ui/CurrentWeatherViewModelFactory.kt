package com.example.weatherappchallenge.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherappchallenge.model.CurrentWeatherRepository
import com.example.weatherappchallenge.util.DataStoreManager

class CurrentWeatherViewModelFactory constructor(
    private val repository: CurrentWeatherRepository,
    private val dataStoreManager: DataStoreManager
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(CurrentWeatherViewModel::class.java)) {
            CurrentWeatherViewModel(this.repository, this.dataStoreManager) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}