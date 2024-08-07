package com.example.managementapptask

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managementapptask.databinding.ActivityUserHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class UserHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserHomeBinding
    private lateinit var taskUserAdapter: TaskUserAdapter
    private lateinit var db: AppDatabase
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var gridLayout: GridLayout
    private lateinit var ivNoTasks: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        userRecyclerView = binding.UserRecyclerView
        gridLayout = binding.UserGridLayout
        ivNoTasks = binding.ivNoUserTasks


        val username = PreferenceHelper.getUsername(this) ?: "User"
        binding.UserTvWelcome.text = "Welcome, $username!"


        taskUserAdapter = TaskUserAdapter(mutableListOf()) { task -> showStartTaskPopup(task) }
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = taskUserAdapter


        db = AppDatabase.getDatabase(this)


        setupRecyclerView()
        fetchTasks(username)


        setupFilterButtons(username)
        binding.userBtnLogout.setOnClickListener {
            handleLogout()
        }

    }

    private fun handleLogout() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close the current activity
    }


    private fun setupRecyclerView() {
        val imageResId = R.drawable.ic_no_tasks_image
        val imageAdapter = ImageAdapter(imageResId)
        userRecyclerView.layoutManager = GridLayoutManager(this, 1)
        userRecyclerView.adapter = imageAdapter
    }

    private fun fetchTasks(username: String, status: String? = null) {
        lifecycleScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    db.userDao().getUserByUsername(username)
                }
                val tasks = user?.let {
                    withContext(Dispatchers.IO) {
                        if (status.isNullOrEmpty()) {
                            db.taskDao().getTasksByUserId(it.id)
                        } else {
                            db.taskDao().getTasksByUserIdAndStatus(it.id, status)
                        }
                    }
                } ?: emptyList()
                tasksInGrid(tasks)
            } catch (e: Exception) {
                Log.e("UserHomeActivity", "Error fetching tasks", e)
            }
        }
    }


    private fun setupFilterButtons(username: String) {
        binding.btnFilterTodo.setOnClickListener {
            fetchTasks(username, "Todo")
        }
        binding.btnFilterInProgress.setOnClickListener {
            fetchTasks(username, "In Progress")
        }
        binding.btnFilterCompleted.setOnClickListener {
            fetchTasks(username, "Completed")
        }
    }


    private fun tasksInGrid(tasks: List<Task>) {
        Log.d("UserHomeActivity", "Updating UI with ${tasks.size} tasks")
        gridLayout.removeAllViews() // Clear existing views

        if (tasks.isNotEmpty()) {
            ivNoTasks.visibility = View.INVISIBLE
            gridLayout.visibility = View.VISIBLE
            userRecyclerView.visibility = View.GONE

            val inflater = LayoutInflater.from(this)
            tasks.forEach { task ->
                Log.d(
                    "UserHomeActivity",
                    "Task: ${task.taskTitle}, Created Date: ${task.createdDate}, Priority: ${task.priority}"
                )

                val taskView = inflater.inflate(R.layout.user_grid_items, gridLayout, false)

                val taskName = taskView.findViewById<TextView>(R.id.tn2)
                val taskCreatedDate = taskView.findViewById<TextView>(R.id.tcd2)
                val dueDateLayout = taskName.findViewById<LinearLayout>(R.id.tcDue)
                val taskStatus = taskView.findViewById<TextView>(R.id.statusValue)
                val taskDueDate = taskView.findViewById<TextView>(R.id.tcDue2)
                val startButton = taskView.findViewById<TextView>(R.id.st)

                taskName.text = task.taskTitle
                when (task.taskStatus) {
                    "Completed" -> taskName.setTextColor(Color.GREEN)
                }
                if ((task.dueDate).toString().isNotEmpty()) {
                    var result = isFirstDateEarlier(task.dueDate.toString(), getCurrentDateTime())
                    when (result) {
                        true -> taskName.setTextColor(Color.RED)
                        false -> taskName.setTextColor(Color.BLACK)
                    }
                }
                taskCreatedDate.text = task.createdDate
                taskStatus.text = task.taskStatus
                when (task.taskStatus) {
                    "Todo" -> taskStatus.setTextColor(Color.BLUE)
                    "In Progress" -> taskStatus.setTextColor(Color.parseColor("#FFA500"))
                    "Completed" -> taskStatus.setTextColor(Color.GREEN)
                    else -> taskStatus.setTextColor(Color.GRAY)
                }
                if (task.dueDate != null) {
                    taskDueDate.text = task.dueDate
                } else {
                    dueDateLayout.visibility == View.INVISIBLE
                }

                startButton.setOnClickListener {
                    if (task.dueDate.toString().isEmpty()) {
                        showStartTaskPopup(task)
                    }
                }

                taskStatus.setOnClickListener {
                    showTaskStatusUpdatePopup(task)
                }


                gridLayout.addView(taskView)
            }
        } else {

            gridLayout.visibility = View.VISIBLE
            userRecyclerView.visibility = View.GONE
        }
    }

    fun isFirstDateEarlier(
        firstDate: String, secondDate: String
    ): Boolean {
        val formatter = DateTimeFormatter.ofPattern(
            "d/M/yyyy"
        )
        val date1 = LocalDate.parse(firstDate, formatter)
        val date2 = LocalDate.parse(secondDate, formatter)
        println("---------------------------$date1,$date2")
        return date1.isBefore(date2)
    }

    private fun getCurrentDateTime(): String {
        val formatter = SimpleDateFormat("d/M/YYYY", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
    }

    private fun showTaskStatusUpdatePopup(task: Task) {

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_status, null)


        val statusSpinner = dialogView.findViewById<Spinner>(R.id.statusSpinner)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)


        val statusOptions = resources.getStringArray(R.array.status_array)
        val currentStatusIndex = statusOptions.indexOf(task.taskStatus)
        statusSpinner.setSelection(currentStatusIndex)


        val alertDialog =
            AlertDialog.Builder(this).setView(dialogView).setPositiveButton("Submit") { _, _ ->
                val newStatus = statusSpinner.selectedItem.toString()
                updateTaskStatus(task, newStatus)
            }.setNegativeButton("Cancel", null).create()


        closeButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun updateTaskStatus(task: Task, newStatus: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                task.taskStatus = newStatus
                db.taskDao().updateTask(task)
            }
            fetchTasks(PreferenceHelper.getUsername(this@UserHomeActivity) ?: "User")
        }
    }

    private fun showStartTaskPopup(task: Task) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_start_task, null)
        val dueDateTextView = dialogView.findViewById<TextView>(R.id.dueDateTextView)

        val calendar = Calendar.getInstance()

        dueDateTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    dueDateTextView.text = "$dayOfMonth/${month + 1}/$year"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )


            datePickerDialog.datePicker.minDate = calendar.timeInMillis

            datePickerDialog.show()
        }

        AlertDialog.Builder(this).setView(dialogView).setPositiveButton("Submit") { _, _ ->
            val dueDate = dueDateTextView.text.toString()
            updateTaskDetails(task, dueDate)
        }.setNegativeButton("Cancel", null).show()
    }


    private fun updateTaskDetails(task: Task, dueDate: String) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                task.dueDate = dueDate

                db.taskDao().updateTask(task)
            }
            fetchTasks(PreferenceHelper.getUsername(this@UserHomeActivity) ?: "User")
        }
    }
}


