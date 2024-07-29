package com.example.managementapptask

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.managementapptask.Task
import com.example.managementapptask.TaskDao
import com.example.managementapptask.User
import com.example.managementapptask.UserDao

@Database(entities = [User::class, Task::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `description` TEXT, `userId` INTEGER NOT NULL)")
            }
        }
    }
}




//package com.example.managementapptask
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//
//@Database(entities = [User::class, Task::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun userDao(): UserDao
//    abstract fun taskDao(): TaskDao
//
//   companion object  {
//       @Volatile
//        private var appDatabase: AppDatabase? = null
//
//        fun getDatabase(context: Context): AppDatabase {
//            if (appDatabase == null) {
//                appDatabase = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "task_management_db"
//                ).build()
//            }
//            return appDatabase!!
//        }
//    }
//
//}
//
