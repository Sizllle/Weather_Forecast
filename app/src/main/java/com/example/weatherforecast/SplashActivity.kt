package com.example.weatherforecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.weatherforecast.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(Sizzle: Bundle?) {
        super.onCreate(Sizzle)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Скрываем Status Bar
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Отчет, после которого открывается другая(выбранная) Activity
        Handler().postDelayed({
            val intent = Intent(this@SplashActivity, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }, 2500)

    }
}