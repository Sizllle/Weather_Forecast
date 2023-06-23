package com.example.weatherforecast.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.models.WeatherWeeklyItem
import com.example.weatherforecast.utils.WeatherUtils
import java.util.Calendar
import java.util.TimeZone

class WeatherWeeklyAdapter(
    private val data: List<WeatherWeeklyItem>,
    private val context: Context
) : RecyclerView.Adapter<WeatherWeeklyAdapter.WeatherWeeklyViewHolder>() {

    // Метод, вызываемый при создании нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherWeeklyAdapter.WeatherWeeklyViewHolder {
        // Создание объекта LayoutInflater из контекста родительского View
        val inflater = LayoutInflater.from(parent.context)
        // Загрузка макета элемента списка item_weather_weekly
        val view = inflater.inflate(R.layout.item_weather_weekly, parent, false)
        // Возвращение нового экземпляра WeatherWeeklyViewHolder, связанного с загруженным макетом
        return WeatherWeeklyViewHolder(view)
    }

    // Метод, вызываемый для связывания данных с ViewHolder
    override fun onBindViewHolder(holder: WeatherWeeklyViewHolder, position: Int) {
        // Получение элемента данных по позиции
        val item = data[position]
        // Вызов метода bind() у ViewHolder для связывания данных
        holder.bind(item, position)
    }

    // Метод, возвращающий количество элементов списка
    override fun getItemCount(): Int {
        // Ограничение количества элементов до 8 или размера списка data
        // В зависимости от того, что меньше
        return minOf(data.size, 8)
    }

    // Внутренний класс ViewHolder, представляющий элемент списка
    inner class WeatherWeeklyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        private val ivWeatherDayOfWeek: ImageView = itemView.findViewById(R.id.ivWeatherDayOfWeek)
        private val tvMaxTemperature: TextView = itemView.findViewById(R.id.tvMaxTemperature)
        private val tvMinTemperature: TextView = itemView.findViewById(R.id.tvMinTemperature)

        // Метод для связывания данных с элементами интерфейса ViewHolder
        fun bind(item: WeatherWeeklyItem, position: Int) {
            // Создание экземпляра локальной временной зоны
            val localTimeZome = TimeZone.getTimeZone("Europe/London")

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = item.date * 1000L

            // Получение дня недели из календаря
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            // Преобразование дня недели в строку для отображения
            val dayOfWeekString = when (dayOfWeek) {
                Calendar.SUNDAY -> "Sunday"
                Calendar.MONDAY -> "Monday"
                Calendar.TUESDAY -> "Tuesday"
                Calendar.WEDNESDAY -> "Wednesday"
                Calendar.THURSDAY -> "Thursday"
                Calendar.FRIDAY -> "Friday"
                Calendar.SATURDAY -> "Saturday"
                else -> "Error"
            }

            if (position == 0) {
                tvDayOfWeek.text = itemView.context.getString(R.string.today)
            } else {
                tvDayOfWeek.text = dayOfWeekString
            }
            // Получение идентификатора изображения погоды для указанного дня
            val weatherImageId = WeatherUtils.getWeatherImageId(context, item.weatherId, item.date, localTimeZome)
            ivWeatherDayOfWeek.setImageResource(weatherImageId)

            val degreeSign = context.getString(R.string.degree_sign)
            tvMaxTemperature.text = "${item.maxTemp}$degreeSign"
            tvMinTemperature.text = "${item.minTemp}$degreeSign"
        }
    }
}