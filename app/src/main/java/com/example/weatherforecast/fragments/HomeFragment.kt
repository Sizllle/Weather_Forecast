package com.example.weatherforecast.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
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
import com.example.weatherforecast.adapters.WeatherWeeklyAdapter
import com.example.weatherforecast.databinding.FragmentHomeBinding
import com.example.weatherforecast.models.WeatherHourlyItem
import com.example.weatherforecast.models.WeatherWeeklyItem
import com.example.weatherforecast.network.OpenWeatherAPI
import com.example.weatherforecast.utils.WeatherUtils
import com.example.weatherforecast.viewmodels.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var weatherHourlyAdapter: WeatherHourlyAdapter
    private lateinit var weatherWeeklyAdapter: WeatherWeeklyAdapter
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
        paintingWordInTextView(isCelsiusSelected)
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
                    val recyclerView = binding.includeWeatherHourly.recyclerViewWeatherHourly
                    // Передаем в adapter список (weatherHourlyList), контекст и temperatureUnit
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
        // Получение еженедельного прогноза погоды от API OpenWeather
        openWeatherAPI.getWeeklyWeather(latitude, longitude, unit) { response ->
            if (response != null) {
                val weeklyWeatherArray = response.getJSONArray("daily")
                val weatherWeeklyList = mutableListOf<WeatherWeeklyItem>()

                // Проход по каждому элементу массива "daily"
                for (i in 0 until weeklyWeatherArray.length()) {
                    val weeklyWeather = weeklyWeatherArray.getJSONObject(i)

                    val date = weeklyWeather.getLong("dt")
                    val weatherId = weeklyWeather.getJSONArray("weather").getJSONObject(0).getInt("id")
                    val maxTemp = weeklyWeather.getJSONObject("temp").getDouble("max").toInt()
                    val minTemp = weeklyWeather.getJSONObject("temp").getDouble("min").toInt()
                    // Создаем объект WeatherWeeklyItem с полученными значениями
                    val weatherWeeklyDayItem = WeatherWeeklyItem(date, weatherId, maxTemp.toString(), minTemp.toString())
                    weatherWeeklyList.add(weatherWeeklyDayItem)
                }
                activity?.runOnUiThread {
                    val recyclerView = binding.includeWeatherWeekly.recyclerViewWeatherWeeklyDay
                    // Передаем в adapter список (weatherWeeklyList) и контекст
                    weatherWeeklyAdapter = WeatherWeeklyAdapter(weatherWeeklyList, requireContext())
                    // Устанавливаем адаптер для RecyclerView
                    recyclerView.adapter = weatherWeeklyAdapter
                    // Устанавливаем LayoutManager для RecyclerView
                    recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
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

    /**
     * Функция для изменения цвета текста в TextView, в зависимости от выбранного режима.
     * Если выбран режим градусов Цельсия, то текст "C°" будет покрашен в colorResId, а текст "F°" будет обычным.
     * Если выбран режим градусов Фаренгейта, то текст "C°" будет обычным, а текст "F°" будет покрашен в colorResId.
     *
     * @param isCelsiusSelected значение, указывающий выбран ли режим градусов Цельсия (true) или Фаренгейта (false)
     */
    private fun paintingWordInTextView(isCelsiusSelected: Boolean) {
        val celsiusText = "C°"
        val fahrenheitText = "F°"
        val colorResId = R.color.temperature_indicator
        // Инициализация объекта для построения Spannable текста
        val spannableBuilder = SpannableStringBuilder()
        if (isCelsiusSelected) { // Если выбраны градусы Цельсия
            // Создание SpannableString для текста градусов Цельсия
            val spannableText = SpannableString(celsiusText)
            spannableText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), colorResId)),
                0,
                spannableText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableBuilder.append(spannableText)
            spannableBuilder.append(" / ")
            spannableBuilder.append(fahrenheitText)
        } else {
            spannableBuilder.append(celsiusText)
            spannableBuilder.append(" / ")
            val spannableText = SpannableString(fahrenheitText)
            spannableText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), colorResId)),
                0,
                spannableText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableBuilder.append(spannableText) // Добавление SpannableString в spannableBuilder
        }
        binding.includeWeatherWeekly.tvTemperatureIndicator.text = spannableBuilder
    }

}