package com.example.careconnect.screens.settings

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


/**
 * ViewModel responsible for general user settings such as signing out.
 *
 * Exposes state indicating when the app should restart (e.g., after sign out).
 *
 * @property authRepository Repository for authentication-related operations.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {

    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    /**
     * Signs out the current user and triggers app restart.
     */
    fun signOut() {
        launchCatching {
            authRepository.signOut()
            println("DEBUG: signed out success, should restart now!!!")
            delay(200)
            _shouldRestartApp.value = true
        }
    }

    /**
     * Resets the restart app state after navigation to splash screen.
     */
    fun onRestart() {
        _shouldRestartApp.value = false
    }
}