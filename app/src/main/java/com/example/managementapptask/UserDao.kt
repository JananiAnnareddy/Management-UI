package com.example.managementapptask

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface UserDao {
    @Insert
     fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND mobile_number = :mobileNumber")
    fun getUser(username: String, mobileNumber: String): User?
}

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE user_id = :userId")
     fun getTasksByUserId(userId: Int): List<Task>
}


