package com.example.weatherforecast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var ivAvatar: ImageView
    lateinit var tvName: TextView
    lateinit var btnLogOut: Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(Sizzle: Bundle?) {
        super.onCreate(Sizzle)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ivAvatar = findViewById(R.id.ivAvatar)
        tvName = findViewById(R.id.tvName)
        btnLogOut = findViewById(R.id.btnLogOut)

        // Инициализируем аутентификацию firebase
        firebaseAuth = FirebaseAuth.getInstance()

        // Инициализируем пользователя firebase
        val firebaseUser = firebaseAuth.currentUser

        // Проверка состояние
        if (firebaseUser != null) {
            Glide.with(this@MainActivity).load(firebaseUser.photoUrl).into(ivAvatar)
            tvName.text = firebaseUser.displayName
        }

        // Инициализируем вход в клиент
        googleSignInClient = GoogleSignIn.getClient(this@MainActivity, GoogleSignInOptions.DEFAULT_SIGN_IN)
        // Выходим из Google аккаунта
        btnLogOut.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener { task ->
                // Проверка состояния
                if (task.isSuccessful) {
                    // Когда задача выполнена, выходим из firebase
                    firebaseAuth.signOut()
                    Toast.makeText(applicationContext, "Logout successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, AuthActivity::class.java)
                    // После перехода на AuthActivity убиваем MainActivity
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }
}