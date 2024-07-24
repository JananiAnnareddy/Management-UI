package com.example.managementapptask

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var imgNoDetails: ImageView
    private lateinit var tableLayout: TableLayout

    companion object {
        const val USER_CREATION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvWelcome = findViewById(R.id.tvWelcome)
        imgNoDetails = findViewById(R.id.imgNoDetails)
        tableLayout = findViewById(R.id.tableLayout)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "User")
        tvWelcome.text = "Welcome, $userName!"

        val fabAddUser = findViewById<FloatingActionButton>(R.id.fabAddUser)
        fabAddUser.setOnClickListener {
            val intent = Intent(this, UserCreationActivity::class.java)
            startActivityForResult(intent, USER_CREATION_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_CREATION_REQUEST_CODE && resultCode == RESULT_OK) {
            val name = data?.getStringExtra("USER_NAME")
            val tasks = data?.getStringExtra("USER_TASKS")
            val rowIndex = data?.getIntExtra("ROW_INDEX", -1) ?: -1

            if (rowIndex == -1) {
                // Hide placeholder image and show table layout
                imgNoDetails.visibility = ImageView.GONE
                tableLayout.visibility = TableLayout.VISIBLE

                // Add user details to the table
                addUserDetailsRow(name, tasks)
            } else {
                // Update the existing row
                updateUserDetailsRow(rowIndex, name, tasks)
            }
        }
    }

    private fun addUserDetailsRow(name: String?, tasks: String?) {
        val tasksList = tasks?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()
        val tasksCount = tasksList.size

        // Inflate the TableRow template
        val userRow = layoutInflater.inflate(R.layout.table_row_template, null) as TableRow

        // Find views within the TableRow
        val nameTextView = userRow.findViewById<TextView>(R.id.tvName)
        val tasksTextView = userRow.findViewById<TextView>(R.id.tvTasks)
        val tasksCountTextView = userRow.findViewById<TextView>(R.id.tvTasksCount)
        val btnEdit = userRow.findViewById<Button>(R.id.btnEdit)
        val btnDelete = userRow.findViewById<Button>(R.id.btnDelete)

        // Set the text for the TextViews
        nameTextView.text = name
        tasksTextView.text = tasks
        tasksCountTextView.text = tasksCount.toString()

        // Set the onClickListeners for the buttons
        btnEdit.setOnClickListener {
            val rowIndex = tableLayout.indexOfChild(userRow)
            val intent = Intent(this@HomeActivity, UserCreationActivity::class.java).apply {
                putExtra("EDIT_MODE", true)
                putExtra("USER_NAME", name)
                putExtra("USER_TASKS", tasks)
                putExtra("ROW_INDEX", rowIndex)
            }
            startActivityForResult(intent, USER_CREATION_REQUEST_CODE)
        }

        btnDelete.setOnClickListener {
            tableLayout.removeView(userRow)
            if (tableLayout.childCount == 1) {
                imgNoDetails.visibility = ImageView.VISIBLE
                tableLayout.visibility = TableLayout.GONE
            }
        }

        // Add the TableRow to the TableLayout
        tableLayout.addView(userRow)
    }

    private fun updateUserDetailsRow(rowIndex: Int, name: String?, tasks: String?) {
        val userRow = tableLayout.getChildAt(rowIndex) as TableRow
        val nameTextView = userRow.findViewById<TextView>(R.id.tvName)
        val tasksTextView = userRow.findViewById<TextView>(R.id.tvTasks)
        val tasksCountTextView = userRow.findViewById<TextView>(R.id.tvTasksCount)

        val tasksList = tasks?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()
        val tasksCount = tasksList.size

        nameTextView.text = name
        tasksTextView.text = tasks
        tasksCountTextView.text = tasksCount.toString()
    }
}
