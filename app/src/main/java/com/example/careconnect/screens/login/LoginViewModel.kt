package com.example.careconnect.screens.login

import com.example.careconnect.MainViewModel
import com.example.careconnect.dataclass.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {

    fun signIn(
        email: String,
        password: String,
        showErrorSnackbar: (ErrorMessage) -> Unit) = launchCatching {
        authRepository.signIn(email, password)
    }
}
