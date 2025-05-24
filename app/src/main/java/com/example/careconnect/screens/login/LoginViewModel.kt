package com.example.careconnect.screens.login

import androidx.credentials.Credential
import androidx.credentials.GetCredentialRequest
import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.SnackBarMessage
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        showSnackBar: (SnackBarMessage) -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.all_fields_required))
            return
        }

        launchCatching(showSnackBar) {
            try {
                authRepository.login(email, password)
                _shouldRestartApp.value = true
            } catch (e: Exception) {
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
                return@launchCatching
            }

        }
    }

    // Google sign-in flows
    private val _googleRequest = MutableSharedFlow<GetCredentialRequest>()
    val googleRequest = _googleRequest.asSharedFlow()

    fun onGoogleSignInClick() {
        launchCatching {
            val request = authRepository.googleLogin()
            _googleRequest.emit(request)

        }
    }

    fun onGoogleCredential(credential: Credential, showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar) {
            try {
                authRepository.handleGoogleLogin(credential)
                _shouldRestartApp.value = true
            } catch (e: Exception) {
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
                return@launchCatching
            }
        }
    }

    private fun getAuthErrorMessage(e: Exception): Int {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> R.string.incorrect_email_or_password
            is FirebaseAuthInvalidUserException -> R.string.incorrect_email_or_password
            is FirebaseNetworkException -> R.string.network_error
            else -> R.string.generic_error
        }
    }

}
