package com.example.managementapptask

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "users", indices = [Index(value = ["mobile_number"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "mobile_number") val mobileNumber: String,
    @ColumnInfo(name = "user_type") val userType: String
)

@Entity(
    tableName = "tasks", foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "taskuser_name") val taskUserName: String,
    @ColumnInfo(name = "phnnumber") val phnnumber: String,
    @ColumnInfo(name = "task_title") val taskTitle: String,
    @ColumnInfo(name = "priority") var priority: String,
    @ColumnInfo(name = "created_date") val createdDate: String,
    @ColumnInfo(name = "created_by") val createBy: String,
    @ColumnInfo(name = "due_date") var dueDate: String?,
    @ColumnInfo(name = "task_status") var taskStatus: String?
)


