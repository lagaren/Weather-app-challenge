package com.example.weatherappchallenge.presentation.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.weatherappchallenge.model.CurrentWeatherRepository
import com.example.weatherappchallenge.network.OpenWeatherApiService
import com.example.weatherappchallenge.presentation.theme.WeatherAppChallengeTheme
import com.example.weatherappchallenge.presentation.ui.CurrentWeatherViewModel
import com.example.weatherappchallenge.util.DataStoreManager

private const val OPEN_WEATHER_IMAGE_BASE_URL = "https://openweathermap.org/img/wn/"


//Composable UI element for the search bar container
//Normally I would put dp values in the dimens file but I just hard coded them for the sake of this exercise.
@Composable
fun SearchBar(
    currentWeatherViewModel: CurrentWeatherViewModel
) {
    Column {
        SearchField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            onSearch = {

                currentWeatherViewModel.getCurrentWeatherByCity(
                    city = it
                )
            },
            currentWeatherViewModel = currentWeatherViewModel
        )
    }
}

//Composable UI element for the search text field
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchField(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentWeatherViewModel: CurrentWeatherViewModel
) {

    val searchState = rememberSaveable {
        mutableStateOf(currentWeatherViewModel.lastCitySearched)
    }

    val isValid = remember(searchState.value) {
        searchState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier,
        value = searchState.value,
        onValueChange = {
            searchState.value = it
        },
        label = {
            Text(text = "Search for a city")
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.clickable {
                    onSearch(searchState.value)
                }
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions {
            if (!isValid) {
                return@KeyboardActions
            } else {
                onSearch(searchState.value)
                searchState.value = ""
                keyboardController?.hide()
            }
        }
    )
}

//Composable UI element for the card where the weather data populates. For the sake of saving time it's always visible and fills out as needed.
@Composable
fun WeatherCard(
    modifier: Modifier,
    currentWeatherViewModel: CurrentWeatherViewModel
) {
    val weatherState by currentWeatherViewModel.currentWeather.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp)
    ) {

        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Column(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {

                if (!weatherState.weatherItems.isNullOrEmpty()) {
                    WeatherImage(imageUrl = "${OPEN_WEATHER_IMAGE_BASE_URL}${weatherState.weatherItems?.first()?.icon}@2x.png")
                    Text(
                        text = weatherState.weatherItems?.first()?.main ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {

                if (!weatherState.weatherItems.isNullOrEmpty()) {
                    Text(
                        text = weatherState.name ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${weatherState.main?.temp.toString()}° F",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Feels like ${weatherState.main?.feelsLike.toString()}° F",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherImage(imageUrl:String,modifier: Modifier = Modifier) {

    Image(painter= rememberImagePainter(imageUrl),contentDescription = "Weather Image",modifier=modifier)
}

@Preview(showBackground = true)
@Composable
fun CurrentWeatherScreenPreview() {
    val currentWeatherViewModel = CurrentWeatherViewModel(
        currentWeatherRepository = CurrentWeatherRepository(
            openWeatherApiService = OpenWeatherApiService.openWeatherApiService
        ),
        dataStoreManager = DataStoreManager(LocalContext.current)
    )

    WeatherAppChallengeTheme {
        WeatherCard(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            currentWeatherViewModel = currentWeatherViewModel
        )

    }
}