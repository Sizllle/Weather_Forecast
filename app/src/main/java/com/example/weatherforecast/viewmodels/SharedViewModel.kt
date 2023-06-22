package com.example.weatherforecast.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    // Приватное поле для хранения информации о выборе пользователя
    private val _isCelsiusSelected = MutableLiveData<Boolean>()

    // Публичное свойство, предостовляющее доступ только для чтения к _isCelsiusSelected
    val isCelsiusSelected: LiveData<Boolean>
        get() = _isCelsiusSelected

    /**
     * Метод для установки значения выбора пользователя
     * @param isCelsiusSelected значение выбора пользователя
     */
    fun setCelsiusSelected(isCelsiusSelected: Boolean) {
        _isCelsiusSelected.value = isCelsiusSelected
    }
}