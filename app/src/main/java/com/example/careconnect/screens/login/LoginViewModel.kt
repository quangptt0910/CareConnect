package com.example.careconnect.screens.login

import android.content.Context
import android.util.Log
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.SnackBarMessage
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
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

    private val _navigateToProfile = MutableStateFlow(false)
    val navigateToProfile: StateFlow<Boolean>
        get() = _navigateToProfile.asStateFlow()

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


    fun onGoogleSignInClick(context: Context, showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar) {
            try {
                authRepository.signInWithGoogle(context)
                val isNewUser = authRepository.patientRecord()

                if (isNewUser) {
                    _navigateToProfile.value = true
                } else {
                    _shouldRestartApp.value = true
                }
            } catch (e: NoCredentialException) {
                Log.e("LoginViewModel", "No Google accounts found", e)
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
            } catch (e: GetCredentialException) {
                Log.e("LoginViewModel", "Google sign-in failed", e)
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
            }
            catch (e: Exception) {
                Log.e("LoginViewModel", "Google sign-in failed", e)
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
            }
        }
    }

    fun resetNavigate() {
        _shouldRestartApp.value = false
        _navigateToProfile.value = false
    }

    private fun getAuthErrorMessage(e: Exception): Int {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> R.string.incorrect_email_or_password
            is FirebaseAuthInvalidUserException -> R.string.incorrect_email_or_password
            is FirebaseNetworkException -> R.string.network_error
            is NoCredentialException -> R.string.no_google_credentials
            is GetCredentialException -> R.string.google_sign_in_failed
            else -> R.string.generic_error
        }
    }

}
