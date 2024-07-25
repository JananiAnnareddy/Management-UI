package com.example.managementapptask

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCreationActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etMobileNumber: EditText
    private lateinit var btnAddTask: Button
    private lateinit var btnSubmit: Button
    private lateinit var tvTaskCount: TextView
    private lateinit var rvTasks: RecyclerView
    private val tasksAdapter = TasksAdapter(mutableListOf())

    private lateinit var userDao: UserDao
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_creation)

        // Initialize DAOs
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        taskDao = db.taskDao()

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = "User Creation Details"

        etUsername = findViewById(R.id.etUsername)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        btnAddTask = findViewById(R.id.btnAddTask)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvTaskCount = findViewById(R.id.tvTaskCount)
        rvTasks = findViewById(R.id.rvTasks)

        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = tasksAdapter

        btnAddTask.setOnClickListener {
            openAddTaskDialog()
        }

        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString()
            val mobileNumber = etMobileNumber.text.toString()
            val tasks = tasksAdapter.getTasks()

            if (username.isNotEmpty() && mobileNumber.isNotEmpty()) {
                // Save user and tasks to the database
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val userId = userDao.insertUser(User(username = username, mobileNumber = mobileNumber))
                        tasks.forEach { task ->
                            taskDao.insertTask(Task(userId = userId.toString(), taskTitle = task))
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@UserCreationActivity, "User and tasks saved successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity and return to previous screen
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@UserCreationActivity, "Error saving data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        tasksAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateTaskCount()
            }
        })
    }

    private fun openAddTaskDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_task)

        val etTaskTitle = dialog.findViewById<EditText>(R.id.etTaskTitle)
        val spPriority = dialog.findViewById<Spinner>(R.id.spPriority)
        val btnAdd = dialog.findViewById<Button>(R.id.btnAdd)

        val priorities = arrayOf("Priority", "Low", "Medium", "High")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spPriority.adapter = adapter

        btnAdd.setOnClickListener {
            val taskTitle = etTaskTitle.text.toString()
            val priority = spPriority.selectedItem.toString()
            if (taskTitle.isNotEmpty()) {
                tasksAdapter.addTask("$taskTitle ($priority)")
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter task title", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateTaskCount() {
        tvTaskCount.text = "Tasks Count: ${tasksAdapter.itemCount}"
    }

    class TasksAdapter(private val tasks: MutableList<String>) :
        RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

        inner class TaskViewHolder(itemView: TextView) : RecyclerView.ViewHolder(itemView) {
            fun bind(task: String) {
                (itemView as TextView).text = task
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val textView = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return TaskViewHolder(textView)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.bind(tasks[position])
        }

        override fun getItemCount(): Int = tasks.size

        fun addTask(task: String) {
            tasks.add(task)
            notifyDataSetChanged()
        }

        fun getTasks(): List<String> = tasks
    }
}
