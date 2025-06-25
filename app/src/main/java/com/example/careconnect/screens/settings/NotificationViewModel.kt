package com.example.careconnect.screens.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.NotificationSettingsRepository
import com.example.careconnect.data.repository.NotificationSettingsResult
import com.example.careconnect.dataclass.Role
import com.example.careconnect.notifications.AppointmentNotificationSettings
import com.example.careconnect.notifications.ChatNotificationSettings
import com.example.careconnect.notifications.NotificationSettings
import com.example.careconnect.notifications.TestNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Represents the UI state for notification settings screen.
 *
 * @property settings The current notification settings.
 * @property isLoading True if settings are being loaded.
 * @property isSaving True if settings are being saved.
 * @property error Error message to display, if any.
 */
data class NotificationSettingsUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel responsible for managing notification settings.
 *
 * It loads notification settings based on the current user role, allows updating chat and appointment
 * notification settings, saving changes, sending test notifications, and handling errors.
 *
 * @property repository Repository to load/save notification settings.
 * @property authRepository Repository to get current user role.
 * @property testNotificationHelper Helper to send test notifications.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationSettingsRepository,
    private val authRepository: AuthRepository,
    private val testNotificationHelper: TestNotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    private val _currentUserRole = MutableStateFlow<Role?>(null)
    val currentUserRole: StateFlow<Role?> = _currentUserRole.asStateFlow()

    init {
        viewModelScope.launch {
            val role = authRepository.getCurrentUserRole()
            _currentUserRole.value = role
            role?.let {
                loadSettingsForRole(it)
            }
        }
    }

    /**
     * Loads notification settings for a given user role.
     *
     * @param role The user role for which settings should be loaded.
     */
    private fun loadSettingsForRole(role: Role) {
        viewModelScope.launch {
            repository.getNotificationSettingsFlow(role).collect { result ->
                when (result) {
                    is NotificationSettingsResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                    }
                    is NotificationSettingsResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            settings = result.settings,
                            isLoading = false,
                            error = null
                        )
                    }
                    is NotificationSettingsResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    /**
     * Updates the chat notification settings and persists the changes.
     *
     * @param chatSettings New chat notification settings.
     */
    fun updateChatSettings(chatSettings: ChatNotificationSettings) {
        val updatedSettings = _uiState.value.settings.copy(chatNotifications = chatSettings)
        saveSettings(updatedSettings)
    }

    /**
     * Updates the appointment notification settings and persists the changes.
     *
     * @param appointmentSettings New appointment notification settings.
     */
    fun updateAppointmentSettings(appointmentSettings: AppointmentNotificationSettings) {
        val updatedSettings = _uiState.value.settings.copy(appointmentNotifications = appointmentSettings)
        saveSettings(updatedSettings)
    }

    /**
     * Saves the provided notification settings to the repository.
     *
     * Handles loading and error states accordingly.
     *
     * @param settings Notification settings to save.
     */
    private fun saveSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            val role = _currentUserRole.value
            if (role == null) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = "User role unknown")
                return@launch
            }

            val result = repository.saveNotificationSettings(role, settings)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(settings = settings, isSaving = false)
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message ?: "Failed to save settings"
                )
            }
        }
    }

    /**
     * Sends a test notification to the device to verify the current notification settings.
     *
     * @param context Android context needed to send the notification.
     */
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

    /**
     * Clears any current error message in the UI state.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
