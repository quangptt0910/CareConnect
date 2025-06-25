package com.example.careconnect.data.repository

import android.util.Log
import com.example.careconnect.data.datasource.NotificationSettingsDataSource
import com.example.careconnect.dataclass.Role
import com.example.careconnect.notifications.NotificationSettings
import com.example.careconnect.notifications.NotificationSettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

sealed class NotificationSettingsResult {
    data class Success(val settings: NotificationSettings) : NotificationSettingsResult()
    data class Error(val message: String) : NotificationSettingsResult()
    object Loading : NotificationSettingsResult()
}

@Singleton
class NotificationSettingsRepository @Inject constructor(
    private val remoteDataSource: NotificationSettingsDataSource,
    private val localSettingsManager: NotificationSettingsManager
) {
    companion object {
        private const val TAG = "NotificationRepo"
    }

    fun getNotificationSettingsFlow(role: Role): Flow<NotificationSettingsResult> {
        return remoteDataSource.getNotificationSettingsFlow(role)
            .map { remoteSettings ->
                if (remoteSettings != null) {
                    localSettingsManager.saveSettings(remoteSettings)
                    Log.d(TAG, "Settings loaded from Firebase and cached locally")
                    NotificationSettingsResult.Success(remoteSettings)
                } else {
                    val localSettings = localSettingsManager.getSettings()
                    Log.d(TAG, "Using local cached settings")
                    NotificationSettingsResult.Success(localSettings)
                }
            }
            .onEach { result ->
                if (result is NotificationSettingsResult.Success) {
                    val remoteSettings = remoteDataSource.getNotificationSettings(role)
                    if (remoteSettings == null) {
                        Log.d(TAG, "First time user - saving default settings to Firebase")
                        try {
                            remoteDataSource.saveNotificationSettings(role, result.settings)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to save default settings to Firebase", e)
                        }
                    }
                }
            }
            .catch { exception ->
                Log.e(TAG, "Error in settings flow", exception)
                val localSettings = localSettingsManager.getSettings()
                emit(NotificationSettingsResult.Success(localSettings))
            }
    }

    suspend fun saveNotificationSettings(role: Role, settings: NotificationSettings): Result<Unit> {
        return try {
            localSettingsManager.saveSettings(settings)
            Log.d(TAG, "Settings saved locally")

            remoteDataSource.saveNotificationSettings(role, settings)
            Log.d(TAG, "Settings saved to Firebase")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving notification settings", e)

            try {
                localSettingsManager.saveSettings(settings)
                Log.d(TAG, "Settings saved locally only due to Firebase error")
                Result.failure(Exception("Settings saved locally only: ${e.message}"))
            } catch (localError: Exception) {
                Log.e(TAG, "Failed to save settings locally too", localError)
                Result.failure(Exception("Failed to save settings: ${e.message}"))
            }
        }
    }

    suspend fun getNotificationSettings(role: Role): NotificationSettings {
        return try {
            val remoteSettings = remoteDataSource.getNotificationSettings(role)
            if (remoteSettings != null) {
                localSettingsManager.saveSettings(remoteSettings)
                remoteSettings
            } else {
                localSettingsManager.getSettings()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting remote settings, using local", e)
            localSettingsManager.getSettings()
        }
    }

    // Local cache utility functions
    fun shouldShowChatNotifications(): Boolean = localSettingsManager.shouldShowChatNotifications()
    fun shouldShowAppointmentNotifications(): Boolean = localSettingsManager.shouldShowAppointmentNotifications()
    fun shouldShowAppointmentType(type: String): Boolean = localSettingsManager.shouldShowAppointmentType(type)
    fun shouldPlaySound(isChat: Boolean): Boolean = localSettingsManager.shouldPlaySound(isChat)
    fun shouldVibrate(isChat: Boolean): Boolean = localSettingsManager.shouldVibrate(isChat)
    fun shouldShowMessagePreview(): Boolean = localSettingsManager.shouldShowMessagePreview()
}
