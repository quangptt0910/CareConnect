package com.example.careconnect.notifications

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "notification_settings"
        private const val KEY_SETTINGS = "settings"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getSettings(): NotificationSettings {
        val json = sharedPreferences.getString(KEY_SETTINGS, null)
        return if (json != null) {
            try {
                gson.fromJson(json, NotificationSettings::class.java)
            } catch (e: Exception) {
                NotificationSettings() // Return default if parsing fails
            }
        } else {
            NotificationSettings() // Return default settings
        }
    }

    fun saveSettings(settings: NotificationSettings) {
        val json = gson.toJson(settings)
        sharedPreferences.edit()
            .putString(KEY_SETTINGS, json)
            .apply()
    }

    // Convenience methods for quick checks in your messaging service
    fun shouldShowChatNotifications(): Boolean = getSettings().chatNotifications.enabled

    fun shouldShowAppointmentNotifications(): Boolean = getSettings().appointmentNotifications.enabled

    fun shouldShowAppointmentType(type: String): Boolean {
        val settings = getSettings().appointmentNotifications
        return when (type.uppercase()) {
            "CONFIRMED", "PENDING" -> settings.confirmations
            "REMINDER" -> settings.reminders
            "CANCELED", "CANCELLED" -> settings.cancellations
            "COMPLETED", "NO_SHOW" -> settings.completions
            else -> true // Show unknown types by default
        }
    }

    fun shouldPlaySound(isChat: Boolean): Boolean {
        return if (isChat) {
            getSettings().chatNotifications.sound
        } else {
            getSettings().appointmentNotifications.sound
        }
    }

    fun shouldVibrate(isChat: Boolean): Boolean {
        return if (isChat) {
            getSettings().chatNotifications.vibration
        } else {
            getSettings().appointmentNotifications.vibration
        }
    }

    fun shouldShowMessagePreview(): Boolean = getSettings().chatNotifications.showPreview
}