package com.example.managementapptask

import androidx.room.*

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "mobile_number") val mobileNumber: String
)

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "task_title") val taskTitle: String,
    @ColumnInfo(name = "priority") val priority: String,
    @ColumnInfo(name = "created_date") val createdDate: String
)

data class UserWithTasks(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id"
    )
    val tasks: List<Task>
)


//package com.example.managementapptask
//
//import androidx.room.*
//
//@Entity(tableName = "users")
//data class User(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    @ColumnInfo(name = "username") val username: String,
//    @ColumnInfo(name = "mobile_number") val mobileNumber: String
//)
//
//@Entity(
//    tableName = "tasks",
//    foreignKeys = [ForeignKey(
//        entity = User::class,
//        parentColumns = ["id"],
//        childColumns = ["user_id"],
//        onDelete = ForeignKey.CASCADE
//    )]
//)
//data class Task(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    @ColumnInfo(name = "user_id") val userId: Int,
//    @ColumnInfo(name = "task_title") val taskTitle: String,
//    @ColumnInfo(name = "priority") val priority: String,
//    @ColumnInfo(name = "created_date") val createdDate: String
//)
//
//data class UserWithTasks(
//    @Embedded val user: User,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "user_id"
//    )
//    val tasks: List<Task>
//)




//package com.example.managementapptask
//
//import androidx.room.ColumnInfo
//import androidx.room.Embedded
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.Relation
//
//
//@Entity(tableName = "users")
//data class User(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    @ColumnInfo(name = "username") val username: String,
//    @ColumnInfo(name = "mobile_number") val mobileNumber: String,
//    //@ColumnInfo(name = "user_type") val usertype: String
//
//    )
//@Entity(tableName = "tasks")
//data class Task(
//    @PrimaryKey(autoGenerate = true) val id: Int = 0,
//    @ColumnInfo(name = "user_id") var userId: String,
//    @ColumnInfo(name = "task_title") val taskTitle: String,
//    @ColumnInfo(name = "priority") val priority: String,
//    @ColumnInfo(name = "created_date") val createdDate: String
//
//)
