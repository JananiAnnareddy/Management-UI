package com.example.managementapptask

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // Initialize the database and userDao
        val db = AppDatabase.getDatabase(this)
        if (db != null) {
            userDao = db.userDao()
        } else {
            Log.e("LoginActivity", "Database initialization failed")
            return
        }

        // Set up listeners
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val name = etName.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()

            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val user = userDao.getUser(name, phoneNumber)
                        if (user != null) {
                            Log.d("LoginActivity", "User found: ${user.username}")

                            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("USER_NAME", user.username)
                            editor.putString("USER_ID", user.id.toString())
                            editor.apply()

                            val intent = Intent(this@LoginActivity, AdminHomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d("LoginActivity", "Invalid credentials")
                            runOnUiThread {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid credentials",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Error during login", e)
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error during login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Please enter both name and phone number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


//package com.example.managementapptask
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.EditText
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//
//class LoginActivity : AppCompatActivity() {
//
//    private lateinit var etName: EditText
//    private lateinit var etPhoneNumber: EditText
//    private lateinit var btnLogin: Button
//    private lateinit var btnRegister: TextView
//    private lateinit var userDao: UserDao
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//        etName = findViewById(R.id.etName)
//        etPhoneNumber = findViewById(R.id.etPhoneNumber)
//        btnLogin = findViewById(R.id.btnLogin)
//        btnRegister = findViewById(R.id.btnRegister)
//
//        val db = AppDatabase.getDatabase(this)
//        userDao = db.userDao()
//
//        btnRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//
//        btnLogin.setOnClickListener {
//            val name = etName.text.toString()
//            val phoneNumber = etPhoneNumber.text.toString()
//
//            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
//                CoroutineScope(Dispatchers.IO).launch {
//                    try {
//                        // Assuming getUser returns a single user or null
//                        val user = userDao.getUser(name, phoneNumber) // Changed to getUser
//                        if (user != null) {
//                            Log.d("LoginActivity", "User found: ${user.username}")
//
//                            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
//                            val editor = sharedPreferences.edit()
//                            editor.putString("USER_NAME", user.username)
//                            editor.putString("USER_ID", user.id.toString())
//                            editor.apply()
//
//                            // Navigate to AdminHomeActivity
//                            val intent = Intent(this@LoginActivity, AdminHomeActivity::class.java)
//                            Log.d(
//                                "LoginActivity",
//                                "Starting intent: ${intent.component?.className}"
//                            )
//                            startActivity(intent)
//                            finish()
//                        } else {
//                            Log.d("LoginActivity", "Invalid credentials")
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@LoginActivity,
//                                    "Invalid credentials",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    } catch (e: Exception) {
//                        Log.e("LoginActivity", "Error during login", e)
//                        runOnUiThread {
//                            Toast.makeText(
//                                this@LoginActivity,
//                                "Error during login",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            } else {
//                Toast.makeText(
//                    this@LoginActivity,
//                    "Please enter both name and phone number",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//
//    }
//}
