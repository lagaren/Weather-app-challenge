package com.example.weatherappchallenge.model.data

import com.google.gson.annotations.SerializedName

data class City (
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("coord") var coord: Coord? = Coord(),
    @SerializedName("country") val country: String = ""
)