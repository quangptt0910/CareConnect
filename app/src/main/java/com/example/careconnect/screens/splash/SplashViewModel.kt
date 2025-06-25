package com.example.careconnect.screens.splash

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.careconnect.MainViewModel
import com.example.careconnect.NotificationData
import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.notifications.FCMTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeout
import javax.inject.Inject


/*
 * ViewModel for the Splash screen.
 * Used to determine the next screen to navigate to based on the user's authentication status and role
 * navigationRoute is a StateFlow that represents the route to navigate to.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val fcmTokenManager: FCMTokenManager
) : MainViewModel() {

    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute = _navigationRoute.asStateFlow()

    private var pendingNotificationData: NotificationData? by mutableStateOf(null)

    init {
        launchCatching {
            fcmTokenManager.debugFCMToken()
            delay(300)
            try {
                val userData = withTimeout(8000) { // 5 second timeout
                    authRepository.currentUserFlow.first { userData ->
                        // Wait for a definitive state (not just first emission)
                        userData != AuthRemoteDataSource.UserData.Error ||
                                authRepository.currentUser == null
                    }
                }
                Log.d("SplashViewModel", "Auth state received: $userData")
                determineNavigationRoute(userData)
            } catch (e: TimeoutCancellationException) {
                Log.e("SplashViewModel", "Timeout waiting for auth state, defaulting to login")
                determineNavigationRoute(AuthRemoteDataSource.UserData.NoUser)
            }
        }
    }

    private fun determineNavigationRoute(userData: AuthRemoteDataSource.UserData) {
        val route = when (userData) {
            is AuthRemoteDataSource.UserData.AdminData -> {
                Log.d("SplashViewModel", "Navigating to admin")
                "admin"
            }
            is AuthRemoteDataSource.UserData.DoctorData -> {
                Log.d("SplashViewModel", "Navigating to doctor")
                "doctor"
            }
            AuthRemoteDataSource.UserData.Error -> {
                Log.d("SplashViewModel", "Auth error, navigating to login")
                "login"
            }
            AuthRemoteDataSource.UserData.NoUser -> {
                Log.d("SplashViewModel", "No user, navigating to login")
                "login"
            }
            is AuthRemoteDataSource.UserData.PatientData -> {
                Log.d("SplashViewModel", "Navigating to patient")
                "patient"
            }
        }
        _navigationRoute.value = route
    }

    fun handleNotificationData(notificationData: NotificationData) {
        this.pendingNotificationData = notificationData
    }

    // New function to get notification data for navigation
    fun getNotificationForNavigation(): NotificationData? {
        return pendingNotificationData?.also { pendingNotificationData = null }
    }
}
