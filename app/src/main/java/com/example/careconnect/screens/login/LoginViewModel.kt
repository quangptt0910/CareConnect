package com.example.careconnect.screens.login

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    fun login(
        email: String,
        password: String,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        launchCatching(showErrorSnackbar) {
            authRepository.login(email, password)
            _shouldRestartApp.value = true
        }
    }
}
