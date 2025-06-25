package com.example.careconnect.screens.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.notifications.NotificationSettings
import com.example.careconnect.notifications.NotificationSettingsManager
import com.example.careconnect.notifications.TestNotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class NotificationSettingsUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaving: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationSettingsManager: NotificationSettingsManager,
    private val testNotificationHelper: TestNotificationHelper,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    private var settingsListener: ListenerRegistration? = null

    companion object {
        private const val TAG = "NotificationViewModel"
        private const val COLLECTION_USERS = "users"
        private const val FIELD_NOTIFICATION_SETTINGS = "notificationSettings"
    }

    init {
        loadSettings()
    }

    private fun loadSettings() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _uiState.value = _uiState.value.copy(
                error = "User not authenticated",
                isLoading = false
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        // Set up real-time listener for settings
        settingsListener = firestore.collection(COLLECTION_USERS)
            .document(currentUser.uid)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to settings changes", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to load settings: ${error.message}"
                    )
                    return@addSnapshotListener
                }

                try {
                    val settings = if (document?.exists() == true) {
                        val settingsMap = document.get(FIELD_NOTIFICATION_SETTINGS) as? Map<String, Any>
                        if (settingsMap != null) {
                            parseSettingsFromMap(settingsMap)
                        } else {
                            // First time user - create default settings
                            val defaultSettings = NotificationSettings()
                            suspend {saveSettingsToFirebase(defaultSettings)}
                            defaultSettings
                        }
                    } else {
                        // Document doesn't exist - create with default settings
                        val defaultSettings = NotificationSettings()
                        suspend {saveSettingsToFirebase(defaultSettings)}
                        defaultSettings
                    }

                    // Also save to local storage for offline access
                    notificationSettingsManager.saveSettings(settings)

                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        isLoading = false,
                        error = null
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing settings", e)
                    // Fallback to local settings
                    val localSettings = notificationSettingsManager.getSettings()
                    _uiState.value = _uiState.value.copy(
                        settings = localSettings,
                        isLoading = false,
                        error = "Using offline settings"
                    )
                }
            }
    }

    fun updateChatSettings(chatSettings: com.example.careconnect.notifications.ChatNotificationSettings) {
        val updatedSettings = _uiState.value.settings.copy(
            chatNotifications = chatSettings
        )
        saveSettings(updatedSettings)
    }

    fun updateAppointmentSettings(appointmentSettings: com.example.careconnect.notifications.AppointmentNotificationSettings) {
        val updatedSettings = _uiState.value.settings.copy(
            appointmentNotifications = appointmentSettings
        )
        saveSettings(updatedSettings)
    }

    private fun saveSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                // Save to Firebase first
                saveSettingsToFirebase(settings)

                // Save locally as backup
                notificationSettingsManager.saveSettings(settings)

                _uiState.value = _uiState.value.copy(
                    settings = settings,
                    isSaving = false,
                    error = null
                )

                Log.d(TAG, "Settings saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save settings", e)

                // Try to save locally at least
                try {
                    notificationSettingsManager.saveSettings(settings)
                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        isSaving = false,
                        error = "Saved locally only - will sync when online"
                    )
                } catch (localE: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Failed to save settings: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun saveSettingsToFirebase(settings: NotificationSettings) {
        val currentUser = auth.currentUser ?: throw Exception("User not authenticated")

        val settingsMap = mapOf(
            FIELD_NOTIFICATION_SETTINGS to mapOf(
                "chatNotifications" to mapOf(
                    "enabled" to settings.chatNotifications.enabled,
                    "sound" to settings.chatNotifications.sound,
                    "vibration" to settings.chatNotifications.vibration,
                    "showPreview" to settings.chatNotifications.showPreview
                ),
                "appointmentNotifications" to mapOf(
                    "enabled" to settings.appointmentNotifications.enabled,
                    "sound" to settings.appointmentNotifications.sound,
                    "vibration" to settings.appointmentNotifications.vibration,
                    "reminders" to settings.appointmentNotifications.reminders,
                    "confirmations" to settings.appointmentNotifications.confirmations,
                    "cancellations" to settings.appointmentNotifications.cancellations,
                    "completions" to settings.appointmentNotifications.completions
                )
            )
        )

        firestore.collection(COLLECTION_USERS)
            .document(currentUser.uid)
            .set(settingsMap, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    private fun parseSettingsFromMap(settingsMap: Map<String, Any>): NotificationSettings {
        val chatMap = settingsMap["chatNotifications"] as? Map<String, Any> ?: emptyMap()
        val appointmentMap = settingsMap["appointmentNotifications"] as? Map<String, Any> ?: emptyMap()

        val chatSettings = com.example.careconnect.notifications.ChatNotificationSettings(
            enabled = chatMap["enabled"] as? Boolean ?: true,
            sound = chatMap["sound"] as? Boolean ?: true,
            vibration = chatMap["vibration"] as? Boolean ?: true,
            showPreview = chatMap["showPreview"] as? Boolean ?: true
        )

        val appointmentSettings = com.example.careconnect.notifications.AppointmentNotificationSettings(
            enabled = appointmentMap["enabled"] as? Boolean ?: true,
            sound = appointmentMap["sound"] as? Boolean ?: true,
            vibration = appointmentMap["vibration"] as? Boolean ?: true,
            reminders = appointmentMap["reminders"] as? Boolean ?: true,
            confirmations = appointmentMap["confirmations"] as? Boolean ?: true,
            cancellations = appointmentMap["cancellations"] as? Boolean ?: true,
            completions = appointmentMap["completions"] as? Boolean ?: true
        )

        return NotificationSettings(
            chatNotifications = chatSettings,
            appointmentNotifications = appointmentSettings
        )
    }

    fun sendTestNotification(context: Context) {
        viewModelScope.launch {
            try {
                testNotificationHelper.sendTestNotification(context, _uiState.value.settings)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to send test notification: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()
        settingsListener?.remove()
    }
}