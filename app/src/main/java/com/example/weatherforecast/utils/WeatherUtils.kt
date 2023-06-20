package com.example.weatherforecast.utils

import android.content.Context
import android.util.Log
import com.example.weatherforecast.R
import java.util.Calendar
import java.util.TimeZone


object WeatherUtils {

    // Метод для определения, является ли текущее время дневным, на основе времени восхода и заката солнца
    private fun isDaytimeBySunriseSunset(sunriseTime: Long, sunsetTime: Long): Boolean {
        val currentTime = System.currentTimeMillis() / 1000 // Получение текущего времени в секундах
        return currentTime in (sunriseTime until sunsetTime) // Проверка, находится ли текущее время между восходом и закатом солнца
    }

    // Метод для определения, является ли указанное время дневным, на основе времени и временной зоны
    private fun isDaytimeByTime(time: Long, timeZone: TimeZone): Boolean {
        val calendar = Calendar.getInstance(timeZone)
        calendar.timeInMillis = time * 1000 // Установка времени в миллисекундах
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // Получение текущего часа дня
        return hour in 6..17 // Проверка, находится ли текущий час в диапазоне 6-17, что считается дневным временем
    }

    // Метод для получения идентификатора изображения погоды на основе времени восхода и заката солнца
    fun getWeatherImageId(context: Context, weatherId: Int, sunriseTime: Long, sunsetTime: Long): Int {
        val isDaytime: Boolean = isDaytimeBySunriseSunset(sunriseTime, sunsetTime)
        Log.d("isDaytimeBySunriseSunset", "Id: $weatherId, isDaytime: $isDaytime")
        return getWeatherImageIdByDaytime(context, weatherId, isDaytime)
    }

    // Метод для получения идентификатора изображения погоды на основе времени и временной зоны
    fun getWeatherImageId(context: Context, weatherId: Int, time: Long, timeZone: TimeZone): Int {
        val isDaytime: Boolean = isDaytimeByTime(time, timeZone)
        Log.d("isDaytimeByTime", "Id: $weatherId, isDaytime: $isDaytime")
        return getWeatherImageIdByDaytime(context, weatherId, isDaytime)
    }

    // Метод для получения идентификатора изображения погоды на основе условий дневного/ночного времени
    private fun getWeatherImageIdByDaytime(context: Context, weatherId: Int, isDaytime: Boolean): Int {
        val drawableId = when {
            // Проверка, содержится ли идентификатор погоды в массиве
            isIdInArray(context, weatherId, R.array.weather_ids_thunderstorm) -> R.drawable.thunderstorm
            isIdInArray(context, weatherId, R.array.weather_ids_thunderstorm_with_rain) -> R.drawable.thunderstorm_with_rain
            isIdInArray(context, weatherId, R.array.weather_ids_thunderstorm_with_heavy_rain) -> R.drawable.thunderstorm_with_heavy_rain
            isIdInArray(context, weatherId, R.array.weather_ids_drizzle) -> R.drawable.drizzle
            isIdInArray(context, weatherId, R.array.weather_ids_rain) -> R.drawable.rain
            isIdInArray(context, weatherId, R.array.weather_ids_freezing_rain) -> R.drawable.freezing_rain
            isIdInArray(context, weatherId, R.array.weather_ids_snow) -> R.drawable.snow
            isIdInArray(context, weatherId, R.array.weather_ids_snowfall) -> R.drawable.snowfall
            isIdInArray(context, weatherId, R.array.weather_ids_wet_snow) -> R.drawable.wet_snow
            isIdInArray(context, weatherId, R.array.weather_ids_fog) -> R.drawable.fog
            isIdInArray(context, weatherId, R.array.weather_ids_sand) -> R.drawable.sand
            isIdInArray(context, weatherId, R.array.weather_ids_tornado) -> R.drawable.tornado
            isIdInArray(context, weatherId, R.array.weather_ids_few_clouds) -> {
                if (isDaytime) {
                    R.drawable.few_clouds
                } else {
                    R.drawable.few_clouds_night
                }
            }
            isIdInArray(context, weatherId, R.array.weather_ids_scattered_clouds) -> {
                if (isDaytime) {
                    R.drawable.scattered_clouds
                } else {
                    R.drawable.scattered_clouds_night
                }
            }
            isIdInArray(context, weatherId, R.array.weather_ids_broken_clouds) -> {
                if (isDaytime) {
                    R.drawable.broken_clouds
                } else {
                    R.drawable.broken_clouds_night
                }
            }
            isIdInArray(context, weatherId, R.array.weather_ids_clear) -> {
                if (isDaytime) {
                    R.drawable.clear
                } else {
                    R.drawable.clear_night
                }
            }
            else -> R.drawable.ic_launcher_foreground // Изображение по умолчанию
        }
        return drawableId
    }

    // Метод для проверки наличия идентификатора в заданном массиве
    private fun isIdInArray(context: Context, id: Int, arrayResId: Int): Boolean {
        val resources = context.resources
        val array = resources.getIntArray(arrayResId)
        return array.contains(id)
    }
}