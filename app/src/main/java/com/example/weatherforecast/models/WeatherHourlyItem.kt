package com.example.weatherforecast.models

data class WeatherHourlyItem(
    val time: Long,
    val weatherId: Int,
    val temperature: String
)
