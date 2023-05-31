package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherforecast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(Sizzle: Bundle?) {
        super.onCreate(Sizzle)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}