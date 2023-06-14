package com.example.weatherforecast.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.network.OpenWeatherAPI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var sunriseTime: Long = 0
    private var sunsetTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val apiKey = ""
        val openWeatherAPI = OpenWeatherAPI(apiKey)

        val latitude = 35.6895
        val longitude = 139.6917
        // Отображение текущей даты в заданном формате
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.ENGLISH)
        val formattedDate = dateFormat.format(currentDate)
        binding.includeWeatherCurrent.tvTodaysDate.text = formattedDate.toString()
        // Определение единицы измерения для погодных данных
        val unit = if (true) "metric" else "imperial"
        // Получение текущей погоды от API OpenWeather
        openWeatherAPI.getCurrentWeather(latitude, longitude, unit) { response ->
            // Обработка ответа от API OpenWeather
            if (response != null) {
                val currentWeather  = response.getJSONObject("current")
                val weatherId = currentWeather.getJSONArray("weather").getJSONObject(0).getInt("id")
                val temperature = currentWeather.getInt("temp")
                val weatherDescription = currentWeather.getJSONArray("weather").getJSONObject(0).getString("description")
                val feelsLikeTemperature = currentWeather.getInt("feels_like")
                val windSpeed = currentWeather.getDouble("wind_speed")
                sunriseTime = currentWeather.getLong("sunrise")
                sunsetTime = currentWeather.getLong("sunset")
                val temperatureUnit = if (true) "\u2103" else "\u2109"

                activity?.runOnUiThread {
                    val drawableId = getWeatherImageId(weatherId)
                    binding.includeWeatherCurrent.imWeatherPicture.setImageResource(drawableId)
                    binding.includeWeatherCurrent.tvTemperatureNow.text = "${temperature}$temperatureUnit"
                    binding.includeWeatherCurrent.tvWeatherDescription.text = weatherDescription.replaceFirstChar { it.uppercase() }
                    binding.includeWeatherCurrent.tvFeelsLike.text = getString(R.string.feels_like, "$feelsLikeTemperature$temperatureUnit")
                    binding.includeWeatherCurrent.tvWindSpeed.text = getString(R.string.wind, windSpeed.toString())
                }
            } else {
                showToast("Error")
            }
        }
        return binding.root
    }

    private fun showToast(message: String) {
        val context = requireContext()
        activity?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Определение изображения погоды
    private fun getWeatherImageId(weatherId: Int): Int {
        val isDaytime = isDaytime() // Определение, является ли текущее время дневным
        val drawableId = when {
            isIdInArray(weatherId, R.array.weather_ids_thunderstorm) -> R.drawable.thunderstorm
            isIdInArray(weatherId, R.array.weather_ids_thunderstorm_with_rain) -> R.drawable.thunderstorm_with_rain
            isIdInArray(weatherId, R.array.weather_ids_thunderstorm_with_heavy_rain) -> R.drawable.thunderstorm_with_heavy_rain
            isIdInArray(weatherId, R.array.weather_ids_drizzle) -> R.drawable.drizzle
            isIdInArray(weatherId, R.array.weather_ids_rain) -> R.drawable.rain
            isIdInArray(weatherId, R.array.weather_ids_freezing_rain) -> R.drawable.freezing_rain
            isIdInArray(weatherId, R.array.weather_ids_snow) -> R.drawable.snow
            isIdInArray(weatherId, R.array.weather_ids_snowfall) -> R.drawable.snowfall
            isIdInArray(weatherId, R.array.weather_ids_wet_snow) -> R.drawable.wet_snow
            isIdInArray(weatherId, R.array.weather_ids_fog) -> R.drawable.fog
            isIdInArray(weatherId, R.array.weather_ids_sand) -> R.drawable.sand
            isIdInArray(weatherId, R.array.weather_ids_tornado) -> R.drawable.tornado
            isIdInArray(weatherId, R.array.weather_ids_few_clouds) -> {
                if (isDaytime) {
                    R.drawable.few_clouds
                } else {
                    R.drawable.few_clouds_night
                }
            }
            isIdInArray(weatherId, R.array.weather_ids_scattered_clouds) -> {
                if (isDaytime) {
                    R.drawable.scattered_clouds
                } else {
                    R.drawable.scattered_clouds_night
                }
            }
            isIdInArray(weatherId, R.array.weather_ids_broken_clouds) -> {
                if (isDaytime) {
                    R.drawable.broken_clouds
                } else {
                    R.drawable.broken_clouds_night
                }
            }
            isIdInArray(weatherId, R.array.weather_ids_clear) -> {
                if (isDaytime) {
                    R.drawable.clear
                } else {
                    R.drawable.clear_night
                }
            }
            else -> R.drawable.ic_launcher_foreground // Картинка по умолчанию
        }
        return drawableId
    }

    // Проверка, является ли текущее время дневным или ночным
    private fun isDaytime(): Boolean {
        // Текущее время в секундах
        val currentTime = System.currentTimeMillis() / 1000
        // Выполняется проверка, находится ли текущее время (until) в диапазоне
        // Между временем восхода (sunriseTime) и заходом солнца (sunsetTime)
        return currentTime in (sunriseTime until sunsetTime)
    }

    // Проверка наличия идентификатора в массиве
    private fun isIdInArray(id: Int, arrayResId: Int): Boolean {
        val array = resources.getIntArray(arrayResId)
        return array.contains(id)
    }

}