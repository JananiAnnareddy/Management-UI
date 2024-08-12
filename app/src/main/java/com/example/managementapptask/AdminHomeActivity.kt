package com.example.managementapptask

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
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
import android.widget.TableRow

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var gridLayout: GridLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var ivNoTasks: ImageView
    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    companion object {
        const val USER_CREATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        tvWelcome = findViewById(R.id.tvWelcome)
        recyclerView = findViewById(R.id.recyclerView)
        gridLayout = findViewById(R.id.gridLayout)
        ivNoTasks = findViewById(R.id.ivNoTasks)

        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            logout()
        }


        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "User")
        val adminID = sharedPreferences.getInt("USER_ID", 0)

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
        fetchAndDisplayTasks(adminID)
    }

    private fun setupRecyclerView() {
        val imageResId = R.drawable.ic_no_tasks_image
        val imageAdapter = ImageAdapter(imageResId)

        recyclerView.layoutManager = GridLayoutManager(this, 1)
        recyclerView.adapter = imageAdapter
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchAndDisplayTasks(adminId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = taskDao.getTasksForAdmin(adminId)
                Log.d("AdminHomeActivity", "Fetched Tasks: $tasks")

                withContext(Dispatchers.Main) {
                    if (tasks.isNotEmpty()) {

                        displayTasksInGrid(tasks, adminId)

                        ivNoTasks.visibility = View.GONE
                        recyclerView.visibility = View.GONE
                        gridLayout.visibility = View.VISIBLE
                    } else {
                        displayNoTasksFoundMessage()
                        ivNoTasks.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                        gridLayout.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showErrorFetchingTasksMessage()
                }
            }
        }
    }

    private fun displayTasksInGrid(tasks: List<Task>, adminId: Int) {
        gridLayout.removeAllViews()

        val groupedTasks = tasks.groupBy { it.userId }

        for ((userId, userTasks) in groupedTasks) {
            val userName = userTasks.first().taskUserName
            val taskCount = userTasks.size

            val itemView = layoutInflater.inflate(R.layout.grid_item, gridLayout, false)

            val tvName = itemView.findViewById<TextView>(R.id.tvName)
            val tvTaskCount = itemView.findViewById<TextView>(R.id.tvTaskCount)
            val editButton = itemView.findViewById<ImageButton>(R.id.editButton)
            val deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)


            tvName.text = userName
            tvTaskCount.text = "Task Count: $taskCount"

            editButton.setOnClickListener {
                val intent = Intent(this, UserCreationActivity::class.java).apply {
                    putExtra("USER_ID", userId)
                }
                startActivityForResult(intent, USER_CREATION_REQUEST_CODE)
            }

            deleteButton.setOnClickListener {
                deleteUser(userId.toString(), adminId)
            }

            val layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(8, 8, 8, 8)
            }

            itemView.layoutParams = layoutParams
            gridLayout.addView(itemView, layoutParams)

            itemView.setOnLongClickListener {
                showTaskDetailsPopup(userTasks)
                true
            }
        }
    }

    private fun deleteUser(userId: String, adminId: Int) {
        val userIdInt = userId.toIntOrNull() ?: return
        CoroutineScope(Dispatchers.IO).launch {
            taskDao.deleteTasksByUserId(userIdInt.toString())
            userDao.deleteUserById(userIdInt.toString())
            withContext(Dispatchers.Main) {
                refreshGrid(adminId)
            }
        }
    }

    private fun refreshGrid(adminId: Int) {
        fetchAndDisplayTasks(adminId)
    }

    private fun showTaskDetailsPopup(userTasks: List<Task>) {

        val dialogView = layoutInflater.inflate(R.layout.popup_task_details, null)
        val builder = AlertDialog.Builder(this).setView(dialogView).setTitle("Task Details")
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }


        val taskDetailsContainer = dialogView.findViewById<TableLayout>(R.id.tableLayout)


        if (userTasks.isEmpty()) {
            Toast.makeText(this, "No tasks available", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("TaskDetailsPopup", "Tasks: $userTasks")

        for (task in userTasks) {
            val tableRow = TableRow(this)

            val taskName = TextView(this)
            taskName.text = task.taskTitle
            taskName.setPadding(8, 8, 8, 8)

            val createdDate = TextView(this)
            createdDate.text = task.createdDate
            createdDate.setPadding(8, 8, 8, 8)

            val priority = TextView(this)
            priority.text = task.priority
            priority.setPadding(8, 8, 8, 8)

            tableRow.addView(taskName)
            tableRow.addView(createdDate)
            tableRow.addView(priority)

            taskDetailsContainer.addView(tableRow)
        }


        builder.create().show()
    }

    private fun displayNoTasksFoundMessage() {
        Toast.makeText(this, "No users found.", Toast.LENGTH_SHORT).show()
    }

    private fun showErrorFetchingTasksMessage() {
        Toast.makeText(this, "Error fetching users. Please try again.", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_CREATION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val adminID = sharedPreferences.getInt("USER_ID", 0)
            fetchAndDisplayTasks(adminID)
        }
    }

}


