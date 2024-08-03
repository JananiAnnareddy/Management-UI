package com.example.managementapptask


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE username = :name AND mobile_number = :phoneNumber ")
    fun getUser(name: String, phoneNumber: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("DELETE FROM users WHERE username = :userId")
    fun deleteUserById(userId: String)

    @Query("SELECT * FROM users WHERE id = :userId ")
    fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE username = :username ")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE mobile_number = :phoneNumber")
    fun getUserByPhoneNumber(phoneNumber: String): User?
}

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE created_by = :adminId")
    fun getTasksForAdmin(adminId: Int): List<Task>


    @Query("DELETE FROM tasks WHERE user_id = :userId")
    fun deleteTasksByUserId(userId: String)

    @Query("SELECT * FROM tasks WHERE user_id = :userId")
    fun getTasksByUserId(userId: Int): List<Task>

    @Query("SELECT * FROM tasks WHERE task_status = :status")
    fun getTasksByStatus(status: String): List<Task>

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND task_status = :status")
    fun getTasksByUserIdAndStatus(userId: Int, status: String): List<Task>


    @Update
    fun updateTask(task: Task)

}





