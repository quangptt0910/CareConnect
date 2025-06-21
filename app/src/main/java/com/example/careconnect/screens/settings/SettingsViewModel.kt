package com.example.careconnect.screens.settings

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {

    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    fun signOut() {
        launchCatching {
            authRepository.signOut()
            println("DEBUG: signed out success, should restart now!!!")
            delay(200)
            _shouldRestartApp.value = true
        }
    }

    fun onRestart() {
        _shouldRestartApp.value = false
    }
}