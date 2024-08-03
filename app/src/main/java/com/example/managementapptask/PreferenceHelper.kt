package com.example.managementapptask

import android.content.Context

object PreferenceHelper {
    private const val PREFS_NAME = "prefs"
    private const val KEY_USERNAME = "username"

    fun getUsername(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun saveUsername(context: Context, username: String) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(KEY_USERNAME, username)
            apply()
        }
    }


}
