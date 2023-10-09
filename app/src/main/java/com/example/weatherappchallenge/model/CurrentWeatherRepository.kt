package com.example.weatherappchallenge.model

import com.example.weatherappchallenge.network.OpenWeatherApiService

class CurrentWeatherRepository constructor(
    private val openWeatherApiService: OpenWeatherApiService
){

    suspend fun getCurrentWeatherByCoord(lat: String, long: String) =
        openWeatherApiService.getCurrentWeatherByCoord(
            lat = lat,
            long = long
        )

    suspend fun getCurrentWeatherByCity(city: String) =
        openWeatherApiService.getCurrentWeatherByCity(
            city = city
        )

}
