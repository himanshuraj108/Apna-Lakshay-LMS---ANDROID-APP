package com.example.lms_android.data

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "lms_auth_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_NAME = "user_name"

    // Allow it to not crash if init is missed, but ideally it is always called
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }
    }

    fun saveToken(token: String, userName: String) {
        prefs?.edit()
            ?.putString(KEY_TOKEN, token)
            ?.putString(KEY_USER_NAME, userName)
            ?.apply()
    }

    fun getToken(): String? {
        return prefs?.getString(KEY_TOKEN, null)
    }

    fun getUserName(): String {
        return prefs?.getString(KEY_USER_NAME, "Student") ?: "Student"
    }

    fun clear() {
        prefs?.edit()?.clear()?.apply()
    }
}
