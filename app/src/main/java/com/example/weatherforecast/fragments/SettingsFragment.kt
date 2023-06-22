package com.example.weatherforecast.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsBinding
import com.example.weatherforecast.viewmodels.SharedViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        // Инициализация SharedViewModel
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        // Получение SharedPreferences для сохранения настроек
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Проверяем, было ли приложение запущено впервые
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            // Установка значения по умолчанию в режиме Цельсия
            sharedViewModel.setCelsiusSelected(true)
            // Пометка о том, что приложение уже запускалось
            sharedPreferences.edit().putBoolean("isFirstRun", false).apply()
        }

        setupTemperatureModes()
        setupClickListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Восстановление режима температуры при возобновлении фрагмента
        restoreTemperatureMode()
    }

    override fun onPause() {
        super.onPause()
        // Сохранение режима температуры при приостановке фрагмента
        saveTemperatureMode()
        // Удаление значения isFirstRun при приостановке фрагмента
        sharedPreferences.edit().remove("isFirstRun").apply()
    }

    // Настройка режимов температуры
    private fun setupTemperatureModes() {
        sharedViewModel.isCelsiusSelected.observe(viewLifecycleOwner) {isCelsiusSelected->
            if (isCelsiusSelected) {
                // Установка изображений для выбранного режима Цельсия
                binding.ivTempCelsius.setImageResource(R.drawable.checkbox_enabled)
                binding.ivTempFahrenheit.setImageResource(R.drawable.checkbox_disabled)
            } else {
                // Установка изображений для выбранного режима Фаренгейта
                binding.ivTempCelsius.setImageResource(R.drawable.checkbox_disabled)
                binding.ivTempFahrenheit.setImageResource(R.drawable.checkbox_enabled)
            }
        }
    }

    // Настройка обработчиков щелчков
    private fun setupClickListeners() {
        binding.llTempModeCelsius.setOnClickListener {
            // Установка выбранного режима Цельсия
            sharedViewModel.setCelsiusSelected(true)
        }

        binding.llTempModeFahrenheit.setOnClickListener {
            // Установка выбранного режима Фаренгейта
            sharedViewModel.setCelsiusSelected(false)
        }
    }

    // Сохранение режима температуры в SharedPreferences
    private fun saveTemperatureMode() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isCelsiusSelected", sharedViewModel.isCelsiusSelected.value ?: true)
        editor.apply()
    }

    // Восстановление режима температуры из SharedPreferences
    private fun restoreTemperatureMode() {
        val isCelsiusSelected = sharedPreferences.getBoolean("isCelsiusSelected", true)
        if (!sharedPreferences.contains("isCelsiusSelected")) {
            // Установка значения по умолчанию в режиме Цельсия, если настройка отсутствует
            sharedViewModel.setCelsiusSelected(true)
        } else {
            // Восстановление сохраненного режима температуры
            sharedViewModel.setCelsiusSelected(isCelsiusSelected)
        }

    }

}