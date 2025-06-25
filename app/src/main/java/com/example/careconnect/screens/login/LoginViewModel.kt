package com.example.careconnect.screens.login

import android.content.Context
import android.util.Log
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.AuthProvider
import com.example.careconnect.dataclass.SnackBarMessage
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel responsible for managing the login screen's UI state and
 * handling user authentication logic, including email/password login,
 * Google sign-in, navigation state, and error handling.
 *
 * @property shouldRestartApp A [StateFlow] indicating whether the app should restart (navigate to splash).
 * @property navigateToProfile A [StateFlow] indicating whether navigation to profile screen should occur.
 * @property accountLinked A [StateFlow] indicating if email and Google accounts are linked.
 * @property isLoading A [StateFlow] representing the loading state during authentication attempts.
 *
 * @constructor Injects the [AuthRepository] for authentication operations.
 */
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

    private val _accountLinked = MutableStateFlow(false)
    val accountLinked: StateFlow<Boolean>
        get() = _accountLinked.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean>
        get() = _isLoading.asStateFlow()

    /**
     * Attempts to log in the user with provided [email] and [password].
     *
     * Shows snack bar messages via [showSnackBar] for validation errors or authentication failures.
     * Prevents concurrent login attempts while already loading.
     *
     * @param email User's email address.
     * @param password User's password.
     * @param showSnackBar Callback to display snack bar messages.
     */
    fun login(
        email: String,
        password: String,
        showSnackBar: (SnackBarMessage) -> Unit
    ) {
        if (_isLoading.value) return

        if (email.isBlank() || password.isBlank()) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.all_fields_required))
            return
        }

        launchCatching(showSnackBar) {
            try {
                authRepository.login(email, password)
                checkAuthProviders(showSnackBar)
                _shouldRestartApp.value = true
            } catch (e: Exception) {
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
                return@launchCatching
            }

        }
    }

    /**
     * Initiates Google Sign-In flow.
     *
     * Handles authentication, linking accounts if needed,
     * navigates to profile screen if user is new, or restarts app on success.
     * Displays error messages using [showSnackBar].
     *
     * @param context Android [Context] used for sign-in.
     * @param showSnackBar Callback to display snack bar messages.
     */
    fun onGoogleSignInClick(context: Context, showSnackBar: (SnackBarMessage) -> Unit) {
        if (_isLoading.value) return // Prevent multiple sign-in attempts
        launchCatching(showSnackBar) {
            _isLoading.value = true
            try {
                authRepository.signInWithGoogle(context)
                println("Debug: Google sign-in clicked")
                val authProvider = authRepository.checkUserAuthProviders()
                if (authProvider == AuthProvider.BOTH_LINKED) {
                    _accountLinked.value = true
                    showSnackBar(SnackBarMessage.StringMessage("Accounts successfully linked! You can now sign in with either method."))
                }

                val isNewUser = authRepository.patientRecord()

                delay(500)

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
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Google sign-in failed", e)
                val errorMessage = getAuthErrorMessage(e)
                showSnackBar(SnackBarMessage.IdMessage(errorMessage))
            }
        }
    }

    private suspend fun checkAuthProviders(showSnackBar: (SnackBarMessage) -> Unit) {
        try {
            val authProviders = authRepository.checkUserAuthProviders()

            when (authProviders) {
                AuthProvider.BOTH_LINKED -> {
                    Log.d("LoginViewModel", "User has both email and Google authentication")
                    authRepository.ensureMergedPatientRecordExists()
                }
                AuthProvider.EMAIL_ONLY -> {
                    Log.d("LoginViewModel", "User has only email authentication")
                    // Optionally prompt user to add Google sign-in as backup
                }
                AuthProvider.GOOGLE_ONLY -> {
                    Log.d("LoginViewModel", "User has only Google authentication")
                    // Optionally prompt user to add password as backup
                }
                else -> {
                    Log.d("LoginViewModel", "Unknown authentication state")
                }
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error checking auth providers", e)
        }
    }

    /**
     * Resets navigation-related flags to allow repeated navigation actions.
     */
    fun resetNavigate() {
        Log.d("LoginViewModel", "Resetting navigation flags")
        _shouldRestartApp.value = false
        _navigateToProfile.value = false
        _accountLinked.value = false
    }

    private fun getAuthErrorMessage(e: Exception): Int {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> R.string.incorrect_email_or_password
            is FirebaseAuthInvalidUserException -> R.string.incorrect_email_or_password
            is FirebaseNetworkException -> R.string.network_error
            is NoCredentialException -> R.string.no_google_credentials
            is GetCredentialException -> R.string.google_sign_in_failed
            is NullPointerException -> R.string.no_google_credentials
            else -> R.string.generic_error
        }
    }

}
