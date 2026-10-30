package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferences(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("kharcha_user_prefs", Context.MODE_PRIVATE)

  private val _language = MutableStateFlow(prefs.getString(KEY_LANGUAGE, "en") ?: "en")
  val language: StateFlow<String> = _language.asStateFlow()

  private val _isAuthenticated = MutableStateFlow(prefs.getBoolean(KEY_IS_AUTH, false))
  val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "User") ?: "User")
  val userName: StateFlow<String> = _userName.asStateFlow()

  private val _userPhone = MutableStateFlow(prefs.getString(KEY_USER_PHONE, "") ?: "")
  val userPhone: StateFlow<String> = _userPhone.asStateFlow()

  private val _isOnboardingDone = MutableStateFlow(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
  val isOnboardingDone: StateFlow<Boolean> = _isOnboardingDone.asStateFlow()

  fun setLanguage(lang: String) {
    prefs.edit().putString(KEY_LANGUAGE, lang).apply()
    _language.value = lang
  }

  fun toggleLanguage() {
    val next = if (_language.value == "hi") "en" else "hi"
    setLanguage(next)
  }

  fun setOnboardingDone(done: Boolean) {
    prefs.edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    _isOnboardingDone.value = done
  }

  fun setAuthenticated(name: String, phone: String) {
    prefs.edit()
      .putBoolean(KEY_IS_AUTH, true)
      .putString(KEY_USER_NAME, name)
      .putString(KEY_USER_PHONE, phone)
      .putBoolean(KEY_ONBOARDING_DONE, true)
      .apply()
    _isAuthenticated.value = true
    _userName.value = name
    _userPhone.value = phone
    _isOnboardingDone.value = true
  }

  fun logout() {
    prefs.edit()
      .putBoolean(KEY_IS_AUTH, false)
      .putString(KEY_USER_NAME, "")
      .putString(KEY_USER_PHONE, "")
      .apply()
    _isAuthenticated.value = false
    _userName.value = ""
    _userPhone.value = ""
  }

  fun resetAll() {
    prefs.edit().clear().apply()
    _language.value = "en"
    _isAuthenticated.value = false
    _userName.value = ""
    _userPhone.value = ""
    _isOnboardingDone.value = false
  }

  companion object {
    private const val KEY_LANGUAGE = "key_language"
    private const val KEY_IS_AUTH = "key_is_auth"
    private const val KEY_USER_NAME = "key_user_name"
    private const val KEY_USER_PHONE = "key_user_phone"
    private const val KEY_ONBOARDING_DONE = "key_onboarding_done"
  }
}
