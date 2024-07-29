package com.example.managementapptask

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var gridLayout: GridLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    companion object {
        const val USER_CREATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvWelcome = findViewById(R.id.tvWelcome)
        recyclerView = findViewById(R.id.recyclerView)
        gridLayout = findViewById(R.id.gridLayout)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "User")
        tvWelcome.text = "Welcome, $userName!"

        val fabAddUser = findViewById<FloatingActionButton>(R.id.fabAddUser)
        fabAddUser.setOnClickListener {
            val intent = Intent(this, UserCreationActivity::class.java)
            startActivityForResult(intent, USER_CREATION_REQUEST_CODE)
        }

        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        taskDao = db.taskDao()

        setupRecyclerView()
        fetchAndDisplayUsers() // Fetch users without passing sample data
    }

    private fun setupRecyclerView() {
        // Initialize ImageAdapter with your image resource ID
        val imageResId = R.drawable.notasksimage // Replace with your actual image resource
        val imageAdapter = ImageAdapter(imageResId)

        recyclerView.layoutManager = GridLayoutManager(this, 1) // Grid layout with 1 column for image
        recyclerView.adapter = imageAdapter
        Log.d("AdminHomeActivity", "RecyclerView setup with ImageAdapter.")
    }

    private fun fetchAndDisplayUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val users = userDao.getAllUsers() // Replace with your actual method to get all users
                withContext(Dispatchers.Main) {
                    if (users.isNotEmpty()) {
                        displayUsersInGrid(users)
                    } else {
                        displayNoUsersFoundMessage()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Optionally, handle the error in a user-friendly way
                showErrorFetchingUsersMessage()
            }
        }
    }

    private fun displayUsersInGrid(users: List<User>) {
        gridLayout.removeAllViews()

        for (user in users) {
            val textView = TextView(this)
            textView.text = "Name: ${user.username}\nMobile: ${user.mobileNumber}"
            textView.setPadding(16, 16, 16, 16)
            textView.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light))
            textView.setTextColor(resources.getColor(android.R.color.white))
            gridLayout.addView(textView)
        }
    }

    private fun displayNoUsersFoundMessage() {
        Toast.makeText(this, "No users found.", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorFetchingUsersMessage() {
        Toast.makeText(this, "Error fetching users. Please try again.", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_CREATION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fetch and display users again after a new user is created
            fetchAndDisplayUsers()
        }
    }
}

data class UserTaskDetails(val user: User, val tasks: List<Task>)

class UserAdapter(private val userTaskDetails: MutableList<UserTaskDetails>) : RecyclerView.Adapter<UserAdapter.UserTaskViewHolder>() {

    inner class UserTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvTasks: TextView = itemView.findViewById(R.id.tvTasks)

        fun bind(userTaskDetail: UserTaskDetails) {
            tvUsername.text = userTaskDetail.user.username
            tvTasks.text = userTaskDetail.tasks.joinToString { it.taskTitle }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_task, parent, false)
        return UserTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserTaskViewHolder, position: Int) {
        holder.bind(userTaskDetails[position])
    }

    override fun getItemCount(): Int = userTaskDetails.size

    fun setUserTaskDetails(details: List<UserTaskDetails>) {
        userTaskDetails.clear()
        userTaskDetails.addAll(details)
        notifyDataSetChanged()
    }
}
