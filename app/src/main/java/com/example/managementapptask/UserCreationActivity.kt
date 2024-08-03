package com.example.managementapptask


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    private var isEditing = false
    private var userId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_creation)
        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        btnAddTask = findViewById(R.id.btnAddTask)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvTaskCount = findViewById(R.id.tvTaskCount)
        rvTasks = findViewById(R.id.rvTasks)
        // Setup RecyclerView
        rvTasks.layoutManager = LinearLayoutManager(this)
        rvTasks.adapter = tasksAdapter
        // Initialize DAO
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        taskDao = db.taskDao()
        // Check if activity is in edit mode
        isEditing = intent.hasExtra("USER_ID")
        userId = intent.getIntExtra("USER_ID", -1)
        if (isEditing) {
            loadUserData(userId!!)
        }
        // Set listeners
        btnAddTask.setOnClickListener {
            openAddTaskDialog()
        }
        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString()
            val mobileNumber = etMobileNumber.text.toString()
            val tasks = tasksAdapter.getTasks()
            if (username.isNotEmpty() && mobileNumber.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                        val adminID = sharedPreferences.getInt("USER_ID", 0)
                        println("-------------------------$adminID")
                        if (isEditing) {
                            // Update existing user
                            userDao.updateUser(
                                User(
                                    userId!!, username, mobileNumber, userType = "User"
                                )
                            )
                            taskDao.deleteTasksByUserId(userId!!.toString())
                        } else {
                            // Insert new user

                            var user = userDao.getUserByPhoneNumber(mobileNumber)
                            if (user == null) {
                                userId = userDao.insertUser(
                                    User(
                                        username = username,
                                        mobileNumber = mobileNumber,
                                        userType = "User"
                                    )
                                ).toInt()
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        this@UserCreationActivity,
                                        "User already registered with the given phone number",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }


                        }
                        tasks.forEach { task ->
                            val (taskTitle, priority) = parseTask(task)
                            val taskEntity = Task(
                                userId = userId!!,
                                taskUserName = username,
                                phnnumber = mobileNumber,
                                taskTitle = taskTitle,
                                priority = priority,
                                createdDate = getCurrentDateTime(),
                                createBy = "$adminID",
                                dueDate = "",
                                taskStatus = "Todo"
                            )
                            taskDao.insertTask(taskEntity)
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@UserCreationActivity,
                                "User and tasks ${if (isEditing) "updated" else "saved"} successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    } catch (e: Exception) {
                        println("--------888888888--$e")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@UserCreationActivity, "-----$e",
                                //"Error saving data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Update task count on data change
        tasksAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                updateTaskCount()
            }
        })
    }

    private fun loadUserData(userId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val user = userDao.getUserById(userId)
                val tasks = taskDao.getTasksByUserId(userId)
                withContext(Dispatchers.Main) {
                    etUsername.setText(user!!.username)
                    etMobileNumber.setText(user.mobileNumber)
                    tasksAdapter.updateTasks(tasks.map { "${it.taskTitle} (${it.priority})" })
                    updateTaskCount()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UserCreationActivity, "Error loading user data", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun openAddTaskDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add_task)
        val etTaskTitle = dialog.findViewById<EditText>(R.id.etTask)
        val spPriority = dialog.findViewById<Spinner>(R.id.spinnerPriority)
        val btnAdd = dialog.findViewById<Button>(R.id.btnAdd)
        val btnClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val priorities = arrayOf("Select Priority", "Low", "Medium", "High")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spPriority.adapter = adapter
        spPriority.setSelection(0)
        btnAdd.setOnClickListener {
            val taskTitle = etTaskTitle.text.toString()
            val priority = spPriority.selectedItem.toString()
            if (taskTitle.isNotEmpty() && priority != "Select Priority") {
                tasksAdapter.addTask("$taskTitle ($priority)")
                dialog.dismiss()
            } else {
                Toast.makeText(
                    this, "Please enter task title and select a valid priority", Toast.LENGTH_SHORT
                ).show()
            }
        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateTaskCount() {
        tvTaskCount.text = "Tasks Count: ${tasksAdapter.itemCount}"
    }

    private fun parseTask(task: String): Pair<String, String> {
        val regex = Regex("(.+) \\((.+)\\)")
        val matchResult = regex.find(task)
        return if (matchResult != null) {
            val (taskTitle, priority) = matchResult.destructured
            Pair(taskTitle, priority)
        } else {
            Pair("", "") // Or handle the error case as appropriate
        }
    }

    private fun getCurrentDateTime(): String {
        val formatter = SimpleDateFormat("dd/MM/YYYY", Locale.getDefault())
        return formatter.format(Calendar.getInstance().time)
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

        fun updateTasks(newTasks: List<String>) {
            tasks.clear()
            tasks.addAll(newTasks)
            notifyDataSetChanged()
        }

        fun getTasks(): List<String> = tasks.toList()
    }
}




