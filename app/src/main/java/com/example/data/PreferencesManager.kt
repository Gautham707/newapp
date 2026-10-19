package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val userId: Long = 0L,
    val name: String = "",
    val title: String = "Customer Credit Manager",
    val email: String = "",
    val username: String = "",
    val phone: String = "",
    val isLoggedIn: Boolean = false,
    val isDarkMode: Boolean = false,
    val language: String = "English"
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("mydue_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            userId = prefs.getLong("user_id", 0L),
            name = prefs.getString("user_name", "") ?: "",
            title = prefs.getString("user_title", "Customer Credit Manager") ?: "Customer Credit Manager",
            email = prefs.getString("user_email", "") ?: "",
            username = prefs.getString("user_username", "") ?: "",
            phone = prefs.getString("user_phone", "") ?: "",
            isLoggedIn = prefs.getBoolean("is_logged_in", false),
            isDarkMode = prefs.getBoolean("is_dark_mode", false),
            language = prefs.getString("app_language", "English") ?: "English"
        )
    }

    fun setSession(user: UserEntity) {
        prefs.edit()
            .putLong("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_username", user.username)
            .putString("user_phone", user.phone)
            .putBoolean("is_logged_in", true)
            .apply()
        _userProfile.value = _userProfile.value.copy(
            userId = user.id,
            name = user.name,
            email = user.email,
            username = user.username,
            phone = user.phone,
            isLoggedIn = true
        )
    }

    fun clearSession() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
    }

    fun updateProfile(name: String, title: String, email: String, phone: String) {
        prefs.edit()
            .putString("user_name", name)
            .putString("user_title", title)
            .putString("user_email", email)
            .putString("user_phone", phone)
            .apply()
        _userProfile.value = _userProfile.value.copy(
            name = name,
            title = title,
            email = email,
            phone = phone
        )
    }

    fun setDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean("is_dark_mode", enabled).apply()
        _userProfile.value = _userProfile.value.copy(isDarkMode = enabled)
    }

    fun setLanguage(lang: String) {
        prefs.edit().putString("app_language", lang).apply()
        _userProfile.value = _userProfile.value.copy(language = lang)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("is_logged_in", loggedIn).apply()
        _userProfile.value = _userProfile.value.copy(isLoggedIn = loggedIn)
    }
}
