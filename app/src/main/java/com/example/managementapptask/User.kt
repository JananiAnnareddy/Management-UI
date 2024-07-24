package com.example.managementapptask

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class User(
    @PrimaryKey val phoneNumber: String,
    val name: String

)
