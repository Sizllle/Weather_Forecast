package com.example.weatherforecast.models

data class WeatherWeeklyItem(
    val date: Long,
    val weatherId: Int,
    val maxTemp: String,
    val minTemp: String
)
