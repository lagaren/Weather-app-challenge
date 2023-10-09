package com.example.weatherappchallenge.network

import com.example.weatherappchallenge.BuildConfig
import com.example.weatherappchallenge.model.data.CurrentWeather
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApiService {

    @GET("weather?units=imperial")
    suspend fun getCurrentWeatherByCoord(
        @Query("lat") lat: String,
        @Query("lon") long: String
    ): Response<CurrentWeather>

    @GET("weather?units=imperial")
    suspend fun getCurrentWeatherByCity(
        @Query("q") city: String
    ): Response<CurrentWeather>

    companion object {
        private val interceptor = Interceptor {
            val url = it
                .request()
                .url
                .newBuilder()
                .addQueryParameter(
                    name = "appid",
                    value = BuildConfig.OPEN_WEATHER_API_KEY)
                .build()

            val request = it
                .request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor it.proceed(request)

        }

        private val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(interceptor)
            .build()

        private fun retrofitService(): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.OPEN_WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val openWeatherApiService: OpenWeatherApiService by lazy {
            retrofitService().create(OpenWeatherApiService::class.java)
        }
    }

}