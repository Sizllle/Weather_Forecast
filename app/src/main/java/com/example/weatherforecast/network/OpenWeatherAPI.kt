package com.example.weatherforecast.network

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class OpenWeatherAPI (private val apiKey: String) {

    /**
     * Функция для получения текущей погоды
     * @param latitude Широта
     * @param longitude Долгота
     * @param unit Единицы измерения (metric - метрическая система, imperial - американская система)
     * @param callback Callback-функция, которая будет вызвана после получения ответа от API
     */
    fun getCurrentWeather(latitude: Double, longitude: Double, unit: String , callback: (JSONObject?) -> Unit) {
        val url = "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=$unit&exclude={part}&appid=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        // Отправка асинхронного запроса к API OpenWeather
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                val jsonObject = jsonData?.let { JSONObject(it) }
                // Вызов callback-функции с полученными данными о погоде
                callback(jsonObject)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenWeatherAPI", "Failed to fetch weather data: ${e.message}")
                // Вызов callback-функции с параметром null в случае ошибки
                callback(null)
            }
        })
    }

    // Функция для получения почасовой погоды
    fun getHourlyWeather(latitude: Double, longitude: Double, unit: String, callback: (JSONObject?) -> Unit) {
        val url = "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=metric&exclude=current,minutely,daily,alerts&appid=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonData = response.body?.string()
                val jsonObject = jsonData?.let { JSONObject(it) }
                // Вызов callback-функции с полученными данными о погоде
                callback(jsonObject)
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("OpenWeatherAPI", "Failed to fetch hourly weather data: ${e.message}")
                // Вызов callback-функции с параметром null в случае ошибки
                callback(null)
            }
        })
    }


}