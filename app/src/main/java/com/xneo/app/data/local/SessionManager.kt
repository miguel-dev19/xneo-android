package com.xneo.app.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.xneo.app.data.model.User

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "xneo_session", Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER = "user_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    fun saveSession(token: String, user: User) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_USER, Gson().toJson(user))
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    
    fun getUser(): User? {
        val userJson = prefs.getString(KEY_USER, null) ?: return null
        return try {
            Gson().fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    
    fun logout() {
        prefs.edit().clear().apply()
    }
}
