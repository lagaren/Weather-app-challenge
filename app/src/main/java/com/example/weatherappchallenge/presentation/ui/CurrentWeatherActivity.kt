package com.example.weatherappchallenge.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.weatherappchallenge.model.CurrentWeatherRepository
import com.example.weatherappchallenge.network.OpenWeatherApiService
import com.example.weatherappchallenge.presentation.screens.SearchBar
import com.example.weatherappchallenge.presentation.screens.WeatherCard
import com.example.weatherappchallenge.presentation.theme.WeatherAppChallengeTheme
import com.example.weatherappchallenge.util.DataStoreManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var dataStoreManager: DataStoreManager

    private lateinit var currentWeatherViewModel: CurrentWeatherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataStoreManager = DataStoreManager(applicationContext)

        val weatherRepository = CurrentWeatherRepository(
            openWeatherApiService = OpenWeatherApiService.openWeatherApiService
        )

        currentWeatherViewModel = ViewModelProvider(
            owner = this,
            factory = CurrentWeatherViewModelFactory(
                repository = weatherRepository,
                dataStoreManager = dataStoreManager
            )
        )[CurrentWeatherViewModel::class.java]

        currentWeatherViewModel.getLastCitySearched()

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        handleLocationPermissionRequest()

        setContent {
            WeatherAppChallengeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {

                        SearchBar(
                            currentWeatherViewModel = currentWeatherViewModel
                        )

                        WeatherCard(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            currentWeatherViewModel = currentWeatherViewModel
                        )
                    }
                }
            }
        }
    }

    // Request location
    fun requestLocationUpdates(locationListener: (Location) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        locationListener.invoke(it)
                    }
                }
        }

    }

    //Ensure we have location permissions. If we do then automatically call out to the openweather API and display the results
    private fun handleLocationPermissionRequest() {
        val requestSinglePermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                requestLocationUpdates {
                    currentWeatherViewModel.getCurrentWeatherByCoord(
                        lat = it.latitude.toString(),
                        long = it.longitude.toString()
                    )
                }
            }
        }

        requestSinglePermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

}