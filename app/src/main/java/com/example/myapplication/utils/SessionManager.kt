package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

enum class UserRole {
    GUEST, OWNER, CUSTOMER, DRIVER
}

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("dayanacar_user_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_ROLE = "user_role"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_NAME = "name"
        private const val KEY_MOBILE = "mobile"
        private const val KEY_DARK_MODE = "is_dark_mode"
        private const val KEY_NOTIFICATIONS = "is_notifications_enabled"
        private const val KEY_LANGUAGE = "selected_language"
    }

    fun saveSession(role: UserRole, userId: Int = 0, username: String = "", name: String = "", mobile: String = "") {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_ROLE, role.name)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USERNAME, username)
            .putString(KEY_NAME, name)
            .putString(KEY_MOBILE, mobile)
            .apply()
    }

    fun updateUserInfo(name: String, mobile: String) {
        prefs.edit()
            .putString(KEY_NAME, name)
            .putString(KEY_MOBILE, mobile)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getRole(): UserRole {
        val roleStr = prefs.getString(KEY_ROLE, UserRole.GUEST.name)
        return try {
            UserRole.valueOf(roleStr ?: UserRole.GUEST.name)
        } catch (e: Exception) {
            UserRole.GUEST
        }
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 0)
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getName(): String = prefs.getString(KEY_NAME, "User") ?: "User"
    fun getMobile(): String = prefs.getString(KEY_MOBILE, "") ?: ""

    // Settings Preferences
    fun isDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)
    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun isNotificationsEnabled(): Boolean = prefs.getBoolean(KEY_NOTIFICATIONS, true)
    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply()
    }

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "English") ?: "English"
    fun setLanguage(lang: String) {
        prefs.edit().putString(KEY_LANGUAGE, lang).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
