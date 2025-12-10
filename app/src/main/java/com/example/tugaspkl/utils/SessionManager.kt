package com.example.tugaspkl.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    companion object {
        const val IS_LOGGED_IN = "isLoggedIn"
        const val USERNAME = "username"
    }

    fun createLoginSession(username: String) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(USERNAME, username)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME, null)
    }
}