package com.example.weatherappchallenge.model.data

import com.google.gson.annotations.SerializedName

data class Cloud(
    @SerializedName("all") val all: Int? = 0,
)
