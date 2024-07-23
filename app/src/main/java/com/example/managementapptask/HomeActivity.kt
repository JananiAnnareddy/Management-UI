package com.example.managementapptask

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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

                // Add table header if it doesn't already exist
                if (tableLayout.childCount == 0) {
                    addTableHeader()
                }

                // Add user details to the table
                addUserDetailsRow(name, tasks)
            } else {
                // Update the existing row
                updateUserDetailsRow(rowIndex, name, tasks)
            }
        }
    }

    private fun addTableHeader() {
        val headerRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val headers = listOf("Name", "Tasks", "Tasks Count", "Edit", "Delete")
        for (header in headers) {
            val textView = TextView(this).apply {
                text = header
                setPadding(8, 8, 8, 8)
                setTextColor(Color.BLACK)
                setTextSize(18f)
                setBackgroundColor(Color.LTGRAY)
                layoutParams = TableRow.LayoutParams(
                    0,
                    TableRow.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }
            headerRow.addView(textView)
        }
        tableLayout.addView(headerRow)
    }

    private fun addUserDetailsRow(name: String?, tasks: String?) {
        val tasksList = tasks?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()
        val tasksCount = tasksList.size

        val userRow = TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val nameTextView = TextView(this).apply {
            text = name
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val tasksTextView = TextView(this).apply {
            text = tasks
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val tasksCountTextView = TextView(this).apply {
            text = tasksCount.toString()
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val btnEdit = Button(this).apply {
            text = "Edit"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.GREEN)
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.5f
            )
            setOnClickListener {
                val rowIndex = tableLayout.indexOfChild(userRow)
                val intent = Intent(this@HomeActivity, UserCreationActivity::class.java).apply {
                    putExtra("EDIT_MODE", true)
                    putExtra("USER_NAME", name)
                    putExtra("USER_TASKS", tasks)
                    putExtra("ROW_INDEX", rowIndex)
                }
                startActivityForResult(intent, USER_CREATION_REQUEST_CODE)
            }
        }

        val btnDelete = Button(this).apply {
            text = "Delete"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.RED)
            layoutParams = TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                0.5f
            )
            setOnClickListener {
                tableLayout.removeView(userRow)
                if (tableLayout.childCount == 1) { // Only the header row left
                    imgNoDetails.visibility = ImageView.VISIBLE
                    tableLayout.visibility = TableLayout.GONE
                }
            }
        }

        userRow.addView(nameTextView)
        userRow.addView(tasksTextView)
        userRow.addView(tasksCountTextView)
        userRow.addView(btnEdit)
        userRow.addView(btnDelete)

        tableLayout.addView(userRow)
    }

    private fun updateUserDetailsRow(rowIndex: Int, name: String?, tasks: String?) {
        val tasksList = tasks?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()
        val tasksCount = tasksList.size

        val userRow = tableLayout.getChildAt(rowIndex) as TableRow
        (userRow.getChildAt(0) as TextView).text = name
        (userRow.getChildAt(1) as TextView).text = tasks
        (userRow.getChildAt(2) as TextView).text = tasksCount.toString()
    }
}