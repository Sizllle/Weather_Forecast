package com.example.weatherforecast.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityMainBinding
import com.example.weatherforecast.fragments.HomeFragment
import com.example.weatherforecast.fragments.LocationFragment
import com.example.weatherforecast.fragments.NotificationFragment
import com.example.weatherforecast.fragments.SettingsFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Инициализация компонентов пользовательского интерфейса
        val headerView = binding.navView.getHeaderView(0)
        val ivAvatar = headerView.findViewById<ImageView>(R.id.ivAvatar)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)
        val ibLogout = headerView.findViewById<ImageButton>(R.id.ibLogout)
        // Инициализация DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)
        // Инициализация Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Добавление кнопки Burger в ActionBar
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_burger_24)
        }
        // Инициализация NavigationView и установка слушателя навигации
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this@MainActivity)
        // Инициализация ActionBarDrawerToggle для связи DrawerLayout и Toolbar
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)

        // Проверка, если активность запускается впервые, и замена текущего фрагмента на HomeFragment
        if (savedInstanceState == null) {
            // Замена фрагмента в контейнере на HomeFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        // Инициализация аутентификации Firebase
        firebaseAuth = FirebaseAuth.getInstance()
        // Получение текущего пользователя Firebase и отображение его информации
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            Glide.with(this@MainActivity).load(firebaseUser.photoUrl).into(ivAvatar)
            tvUserName.text = firebaseUser.displayName
        }

        // Клиент для входа в Google, используемый для выполнения операции выхода из аккаунта
        googleSignInClient = GoogleSignIn.getClient(this@MainActivity, GoogleSignInOptions.DEFAULT_SIGN_IN)
        ibLogout.setOnClickListener {
            // Вызов метода для отображения диалогового окна подтверждения выхода из аккаунта
            showLogoutConfirmationDialog()
            // Закрытие Navigation Drawer после клика на кнопку
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    // Обработчик выбора элемента меню в NavigationView
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_location -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LocationFragment()).commit()
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
            R.id.nav_notification -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationFragment()).commit()
            R.id.nav_settings -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment()).commit()
        }
        // Закрытие Navigation Drawer после выбора элемента
        drawerLayout.closeDrawer(GravityCompat.START)
        return  true
    }

    // Обработчик нажатия системной кнопки "Назад"
    override fun onBackPressed() {
        // Если Navigation Drawer открыт, закрываем его
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Иначе, вызываем обработчик системной кнопки "Назад"
            onBackPressedDispatcher.onBackPressed()
        }
    }

    /**
     * Отображает диалоговое окно подтверждения выхода из аккаунта.
     * При нажатии ImageButton Logout, данное диалоговое окно запрашивает у пользователя подтверждение на выход из его аккаунта.
     * Если пользователь подтверждает, происходит выход из аккаунта Google и Firebase, а затем происходит перенаправление на AuthActivity.
     * Если пользователь отменяет действие, диалоговое окно закрывается без выполнения дополнительных действий.
     */
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Log out")
        builder.setMessage("Are you sure you want to log out of your account?")
        builder.setPositiveButton("Log out") { dialog, which ->
            // Обработчик события выхода из Google аккаунта
            googleSignInClient.signOut().addOnCompleteListener { task ->
                // Проверка состояния выполнения операции выхода из аккаунта
                if (task.isSuccessful) {
                    // Выход из Firebase после успешного выхода из Google аккаунта
                    firebaseAuth.signOut()
                    Toast.makeText(applicationContext, "Logout successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, AuthActivity::class.java)
                    // После перехода на AuthActivity убиваем MainActivity
                    startActivity(intent)
                    finish()
                }
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

}