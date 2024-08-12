package com.example.managementapptask

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            var userRole = sharedPreferences.getString("USER_ROLE", null)
            if (userRole == "Admin") {
                val intent = Intent(this@SplashActivity, AdminHomeActivity::class.java)
                startActivity(intent)
                finish()
            } else if (userRole == "User") {
                var intent = Intent(this@SplashActivity, UserHomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                var intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}