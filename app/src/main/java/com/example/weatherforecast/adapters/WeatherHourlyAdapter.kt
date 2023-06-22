package com.example.weatherforecast.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.models.WeatherHourlyItem
import com.example.weatherforecast.utils.WeatherUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WeatherHourlyAdapter(
    private val data: List<WeatherHourlyItem>,
    private val context: Context,
    private val temperatureUnit: String
    ) : RecyclerView.Adapter<WeatherHourlyAdapter.WeatherHourlyViewHolder>() {

    // Метод, вызываемый при создании нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHourlyAdapter.WeatherHourlyViewHolder {
        // Создание объекта LayoutInflater из контекста родительского View
        val inflater = LayoutInflater.from(parent.context)
        // Загрузка макета элемента списка item_weather_hourly
        val view = inflater.inflate(R.layout.item_weather_hourly, parent, false)
        // Возвращение нового экземпляра WeatherHourlyViewHolder, связанного с загруженным макетом
        return WeatherHourlyViewHolder(view)
    }

    // Метод, вызываемый для связывания данных с ViewHolder
    override fun onBindViewHolder(holder: WeatherHourlyViewHolder, position: Int) {
        // Получение элемента данных по позиции
        val item = data[position]
        // Вызов метода bind() у ViewHolder для связывания данных
        holder.bind(item, position)
    }

    // Метод, возвращающий количество элементов списка
    override fun getItemCount(): Int {
        // Ограничение количества элементов до 25 или размера списка data, в зависимости от того, что меньше
        return minOf(data.size, 25)
    }

    // Внутренний класс ViewHolder, представляющий элемент списка
    inner class WeatherHourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTime: TextView = itemView.findViewById(R.id.tvTimeHourly)
        private val ivWeather: ImageView = itemView.findViewById(R.id.ivWeatherHourly)
        private val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperatureHourly)

        // Метод для связывания данных с элементами интерфейса ViewHolder
        fun bind(item: WeatherHourlyItem, position: Int) {
            // Преобразование UTC-времени в локальное время

            // Создание экземпляра временной зоны UTC
            val utcTimeZone = TimeZone.getTimeZone("UTC")
            // Создание экземпляра локальной временной зоны
            val localTimeZome = TimeZone.getTimeZone("Europe/London")
            // Создание формата отображения даты и времени
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            // Установка локальной временной зоны для формата
            dateFormat.timeZone = localTimeZome
            // Преобразование UTC-времени в локальное время
            val localTime = convertUtcToLocal(item.time, utcTimeZone)
            // Форматирование локального времени в строку
            val timeString = dateFormat.format(localTime)

            if (position == 0) {
                tvTime.text = itemView.context.getString(R.string.now)
            } else {
                tvTime.text = timeString
            }

            // Получение и установка идентификатора изображения погоды в ImageView ivWeather
            val weatherImageId = WeatherUtils.getWeatherImageId(context, item.weatherId, item.time, localTimeZome)
            ivWeather.setImageResource(weatherImageId)

            tvTemperature.text = "${item.temperature}$temperatureUnit"
        }

        // Метод для преобразования времени из секунд в формат Date с учетом временной зоны
        fun convertUtcToLocal(timeInSecond: Long, timeZone: TimeZone): Date {
            val timeInMillis = timeInSecond * 1000 // Преобразуем время в миллисекунды

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis
            calendar.timeZone = timeZone

            return calendar.time
        }
    }
}