package com.example.managementapptask

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserDao {
    @Insert
    fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :name AND mobile_number = :phoneNumber LIMIT 1")
    fun getUser(name: String, phoneNumber: String): User?

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserWithTasks(userId: Int): UserWithTasks
}

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE user_id = :userId")
    fun getTasksForUser(userId: String): List<Task>
}




//package com.example.managementapptask
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//import androidx.room.Transaction
//
//
//@Dao
//interface UserDao {
//    @Insert
//    fun insertUser(user: User)
//
//    @Query("SELECT * FROM users WHERE username = :name AND mobile_number = :phoneNumber LIMIT 1")
//    fun getUser(name: String, phoneNumber: String): User?
//
//    @Query("SELECT * FROM users")
//    fun getAllUsers(): List<User>
//
//    @Transaction
//    @Query("SELECT * FROM users WHERE id = :userId")
//    fun getUserWithTasks(userId: Int): UserWithTasks
//}
//
//@Dao
//interface TaskDao {
//    @Insert
//    fun insertTask(task: Task)
//
//    @Query("SELECT * FROM tasks WHERE user_id = :userId")
//    fun getTasksForUser(userId: Int): List<Task>
//}




//@Dao
//interface UserDao {
//    @Insert
//    fun insertUser(user: User)
//
//    @Query("SELECT * FROM users WHERE username = :name AND mobile_number = :phoneNumber LIMIT 1")
//     fun getUser(name: String, phoneNumber: String): User?
//
//    @Query("SELECT * FROM users")
//    fun getAllUsers(): List<User>
//
//
//
//
//}
//
//@Dao
//interface TaskDao {
//    @Insert
//    fun insertTask(task: Task)
//
//    @Query("SELECT * FROM tasks WHERE user_id= :userId")
//     fun getTasksForUser(userId: String): List<Task>
//
//
//}



//@Dao
//interface UserDao {
//    @Insert
//    fun insertUser(user: User): Long
//
//    @Query("SELECT * FROM users")
//    fun getAllUsers(): List<User>
//}


//@Dao
//interface TaskDao {
//    @Insert
//    fun insertTask(task: Task)
//
//    @Query("SELECT * FROM tasks WHERE user_id = :userId")
//     fun getTasksByUserId(userId: String): List<Task>
//
//
//}
//


