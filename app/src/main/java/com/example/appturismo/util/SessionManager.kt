package com.example.appturismo.util

import android.content.Context
import android.content.SharedPreferences
import com.example.appturismo.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        val userJson = gson.toJson(user)
        prefs.edit().putString("current_user", userJson).apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString("current_user", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else null
    }

    fun logout() {
        prefs.edit().remove("current_user").apply()
    }
}
