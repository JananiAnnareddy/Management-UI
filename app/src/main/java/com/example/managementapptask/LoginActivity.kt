package com.example.managementapptask


import android.content.Intent
import android.os.Bundle
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
        userDao = db.userDao()

        // Set up listeners
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }



        btnLogin.setOnClickListener {
            val name = etName.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()

            if (name.isNotEmpty() && phoneNumber.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val user = userDao.getUser(name, phoneNumber)
                    if (user != null) {
                        println("user---------------------------------- =$user")
                        // Save the username using PreferenceHelper
                        PreferenceHelper.saveUsername(this@LoginActivity, user.username)

                        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("USER_NAME", user.username)
                        editor.putInt("USER_ID", user.id) // Store user ID
                        editor.apply()
                        if (user.userType == "Admin") {
                            val intent = Intent(this@LoginActivity, AdminHomeActivity::class.java)
                            intent.putExtra("USER_ID", user.id) // Pass user ID to the next activity
                            startActivity(intent)
                        } else {

                            val intent = Intent(this@LoginActivity, UserHomeActivity::class.java)
                            intent.putExtra("USER_ID", user.id) // Pass user ID to the next activity
                            startActivity(intent)
                        }


                        finish()
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}



