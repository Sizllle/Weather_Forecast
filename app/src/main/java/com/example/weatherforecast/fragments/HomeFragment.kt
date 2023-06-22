package com.example.weatherforecast.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.R
import com.example.weatherforecast.adapters.WeatherHourlyAdapter
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.models.WeatherHourlyItem
import com.example.weatherforecast.network.OpenWeatherAPI
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.viewmodels.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var weatherHourlyAdapter: WeatherHourlyAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var sunriseTime: Long = 0
    private var sunsetTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val apiKey = ""
        val openWeatherAPI = OpenWeatherAPI(apiKey)

        val latitude = 51.5074
        val longitude = -0.1278
        // Отображение текущей даты в заданном формате
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM", Locale.ENGLISH)
        val formattedDate = dateFormat.format(currentDate)
        binding.includeWeatherCurrent.tvTodaysDate.text = formattedDate.toString()
        // Получение экземпляра SharedPreferences с именем "MyPrefs"
        // И режимом доступа Context.MODE_PRIVATE
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Получение значения boolean из SharedPreferences с ключом "isCelsiusSelected"
        // Если значение не найдено, будет использовано значение по умолчанию true
        val isCelsiusSelected = sharedPreferences.getBoolean("isCelsiusSelected", true)
        // Определение единицы измерения для погодных данных
        val unit = if (isCelsiusSelected) "metric" else "imperial"
        // Определение переменной temperatureUnit, которая представляет символ единицы измерения температуры
        val temperatureUnit = if (isCelsiusSelected) "\u2103" else "\u2109"
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
                val drawableId = WeatherUtils.getWeatherImageId(requireContext(), weatherId, sunriseTime, sunsetTime)
                val drawable = drawableId?.let { ContextCompat.getDrawable(requireContext(), it) }

                activity?.runOnUiThread {
                    binding.includeWeatherCurrent.ivWeatherPicture.setImageDrawable(drawable)
                    binding.includeWeatherCurrent.tvTemperatureNow.text = "${temperature}$temperatureUnit"
                    binding.includeWeatherCurrent.tvWeatherDescription.text = weatherDescription.replaceFirstChar { it.uppercase() }
                    binding.includeWeatherCurrent.tvFeelsLike.text = getString(R.string.feels_like, "$feelsLikeTemperature$temperatureUnit")
                    binding.includeWeatherCurrent.tvWindSpeed.text = getString(R.string.wind, windSpeed.toString())
                }
            } else {
                showToast("Error, current weather")
            }
        }
        // Получение почасовой погоды от API OpenWeather
        openWeatherAPI.getHourlyWeather(latitude, longitude, unit) { response ->
            if (response != null){
                val hourlyWeatherArray = response.getJSONArray("hourly")
                // Создаем пустой список для хранения объектов WeatherHourlyItem
                val weatherHourlyList = mutableListOf<WeatherHourlyItem>()

                // Проход по каждому элементу массива "hourly"
                for (i in 0 until hourlyWeatherArray.length()) {
                    val hourlyWeather = hourlyWeatherArray.getJSONObject(i)

                    val time = hourlyWeather.getLong("dt")
                    val temperature = hourlyWeather.getInt("temp")
                    val weatherId = hourlyWeather.getJSONArray("weather").getJSONObject(0).getInt("id")
                    // Создаем объект WeatherHourlyItem с полученными значениями
                    val weatherHourlyItem = WeatherHourlyItem(time, weatherId, temperature.toString())
                    weatherHourlyList.add(weatherHourlyItem)
                }
                activity?.runOnUiThread {
                    //
                    val recyclerView = binding.includeWeatherHourly.recyclerViewWeatherHourly
                    // Передаем в adapter список (weatherHourlyList) и контекст
                    weatherHourlyAdapter = WeatherHourlyAdapter(weatherHourlyList, requireContext(), temperatureUnit)
                    // Устанавливаем адаптер для RecyclerView
                    recyclerView.adapter = weatherHourlyAdapter
                    // Устанавливаем LayoutManager для RecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                }
            } else {
                showToast("Error, hourly weather")
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

}