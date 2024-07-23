package com.example.managementapptask

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Query("SELECT * FROM users WHERE name = :name AND phoneNumber = :phoneNumber LIMIT 1")
    fun getUser(name: String, phoneNumber: String): User?
}
