package com.example.weatherforecast.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var btnSignIn: SignInButton
    lateinit var googleSignClient: GoogleSignInClient

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState )
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Устанавливаем цвет текста StatusBar на черный
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        btnSignIn = findViewById(R.id.btnAuthGoogle)
        // Иницифлизируем параметры входа в систему
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("951092010483-idasfme8foto3l5ooj8bf9grbs7j52db.apps.googleusercontent.com")
            .requestEmail()
            .build()

        // Инициализируем вход в клиент
        googleSignClient = GoogleSignIn.getClient(this@AuthActivity, googleSignInOptions)
        btnSignIn.setOnClickListener {// Инициализируем намерение входа
            val intent: Intent = googleSignClient.signInIntent
            // Начинаем работу на резултат
            startActivityForResult(intent, 100)
        }

        // Инициализируем аутентификацию firebase
        firebaseAuth = FirebaseAuth.getInstance()
        // Инициализируем пользователя firebase
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        // Проверяем состояние
        if (firebaseUser != null) {
            // Если пользователь уже вошёл в систему, перенаправить на MainActivity
            startActivity(Intent(this@AuthActivity, MainActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Проверка условия
        if (requestCode == 100) {
            // Когда код запроса равен 100, инициализируем задачу
            val signInAccountTask: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            // Проверка условия
            if (signInAccountTask.isSuccessful) {
                val s = "Google sign in successful"
                displayToast(s)
                // Инициализация входа в учетную запись
                try {
                    // Инициализация входа в учетную запись
                    val googleSignInAccount = signInAccountTask.getResult(ApiException::class.java)
                    // Проверка состояния
                    if (googleSignInAccount != null) {
                        // Когда учетная запись для входа не равна нулю, инициализируем учетные данные для аутентификации
                        val authCredential: AuthCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
                        // Проверка учетных данных
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this) { task ->
                            // Проверка состояния
                            if (task.isSuccessful) {
                                val currentUser = firebaseAuth.currentUser
                                if (currentUser != null) {
                                    // При успешном выполнении задачи перенаправление на MainActivity
                                    startActivity(Intent(this@AuthActivity, MainActivity::class.java)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                                    displayToast("Firebase authentication successful")
                                }
                            } else {
                                // В случае ошибки
                                displayToast("Authentication Failed: ${task.exception?.message}")
                            }
                        }
                    }
                } catch (e: ApiException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun displayToast(s: String) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
    }
}