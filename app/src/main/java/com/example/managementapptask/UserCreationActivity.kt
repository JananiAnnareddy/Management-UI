package com.example.managementapptask

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserCreationActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etTasks: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvTaskCount: TextView
    private var editMode = false
    private var rowIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_creation)

        etFirstName = findViewById(R.id.etFirstName)
        etTasks = findViewById(R.id.etTasks)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvTaskCount = findViewById(R.id.tvTaskCount)

        editMode = intent.getBooleanExtra("EDIT_MODE", false)
        if (editMode) {
            val name = intent.getStringExtra("USER_NAME")
            val tasks = intent.getStringExtra("USER_TASKS")
            rowIndex = intent.getIntExtra("ROW_INDEX", -1)
            etFirstName.setText(name)
            etTasks.setText(tasks)
            updateTaskCount(tasks)
        }

        etTasks.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateTaskCount(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnSubmit.setOnClickListener {
            val name = etFirstName.text.toString()
            val tasks = etTasks.text.toString()

            val resultIntent = Intent().apply {
                putExtra("USER_NAME", name)
                putExtra("USER_TASKS", tasks)
                putExtra("ROW_INDEX", rowIndex)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun updateTaskCount(tasks: String?) {
        val tasksList = tasks?.trim()?.split("\n")?.filter { it.isNotEmpty() } ?: emptyList()
        tvTaskCount.text = "Tasks Count: ${tasksList.size}"
    }
}
