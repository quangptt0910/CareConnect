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

data class NotificationSettingsUiState(
    val settings: NotificationSettings = NotificationSettings(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

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

    fun updateChatSettings(chatSettings: ChatNotificationSettings) {
        val updatedSettings = _uiState.value.settings.copy(chatNotifications = chatSettings)
        saveSettings(updatedSettings)
    }

    fun updateAppointmentSettings(appointmentSettings: AppointmentNotificationSettings) {
        val updatedSettings = _uiState.value.settings.copy(appointmentNotifications = appointmentSettings)
        saveSettings(updatedSettings)
    }

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
}
