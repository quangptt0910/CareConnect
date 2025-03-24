package com.example.careconnect.screens.signup

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    fun signUp(
        name: String,
        surname: String,
        email: String,
        password: String,
        showSnackbar: (ErrorMessage) -> Unit
    ) {
        if (!email.isValidEmail()) {
            showSnackbar(ErrorMessage.IdError(R.string.invalid_email))
            return
        }
        if (!password.isValidPassword()) {
            showSnackbar(ErrorMessage.IdError(R.string.invalid_password))
            return
        }

        launchCatching(showSnackbar) {
            println("Debug: SignUp clicked")
            authRepository.signUp(name, surname, email, password)
            println("Debug: SignUp success")
            _shouldRestartApp.value = true
        }
    }
}

