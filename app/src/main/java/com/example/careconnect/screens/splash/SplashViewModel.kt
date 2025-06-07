package com.example.careconnect.screens.splash

import com.example.careconnect.MainViewModel
import com.example.careconnect.NotificationData
import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.notifications.FCMTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    private var pendingNotificationData: NotificationData? = null


    init {
        launchCatching {
            fcmTokenManager.debugFCMToken()
            val userData = authRepository.currentUserFlow.first()
            println("Debug Splash: $userData")

            val route = if (pendingNotificationData != null && userData != AuthRemoteDataSource.UserData.NoUser) {
                // User is authenticated and we have notification data
                when (pendingNotificationData!!.userType.lowercase()) {
                    "doctor" -> {
                        if (userData is AuthRemoteDataSource.UserData.DoctorData) "doctor"
                        else getDefaultRoute(userData) // Fallback if user type doesn't match
                    }
                    "patient" -> {
                        if (userData is AuthRemoteDataSource.UserData.PatientData) "patient"
                        else getDefaultRoute(userData) // Fallback if user type doesn't match
                    }
                    else -> getDefaultRoute(userData)
                }
            } else {
                getDefaultRoute(userData)
            }

            println("Debug Splash route: $route")
            _navigationRoute.value = route
        }
    }

    private fun getDefaultRoute(userData: AuthRemoteDataSource.UserData): String {
        return when (userData) {
            is AuthRemoteDataSource.UserData.AdminData -> "admin"
            is AuthRemoteDataSource.UserData.DoctorData -> "doctor"
            AuthRemoteDataSource.UserData.Error -> "login"
            AuthRemoteDataSource.UserData.NoUser -> "login"
            is AuthRemoteDataSource.UserData.PatientData -> "patient"
        }
    }

    fun handleNotificationData(notificationData: NotificationData) {
        println("Debug: Handling notification data: $notificationData")
        this.pendingNotificationData = notificationData

        launchCatching {
            val userData = authRepository.currentUserFlow.first()
            if (userData != AuthRemoteDataSource.UserData.NoUser) {
                val route = when (notificationData.userType.lowercase()) {
                    "doctor" -> {
                        if (userData is AuthRemoteDataSource.UserData.DoctorData) "doctor"
                        else getDefaultRoute(userData)
                    }
                    "patient" -> {
                        if (userData is AuthRemoteDataSource.UserData.PatientData) "patient"
                        else getDefaultRoute(userData)
                    }
                    else -> getDefaultRoute(userData)
                }
                println("Debug: Updated route for notification: $route")
                _navigationRoute.value = route
            }
        }
    }
}

//    init {
//        launchCatching {
//
//            fcmTokenManager.debugFCMToken()
//
//            val userData = authRepository.currentUserFlow.first()
//            println("Debug Splash: $userData")
//            val route = when(userData) {
//                is AuthRemoteDataSource.UserData.AdminData -> "admin"
//                is AuthRemoteDataSource.UserData.DoctorData -> "doctor"
//                AuthRemoteDataSource.UserData.Error -> "login"
//                AuthRemoteDataSource.UserData.NoUser -> "login"
//                is AuthRemoteDataSource.UserData.PatientData -> "patient"
//            }
//            println("Debug Splash route: $route")
//            _navigationRoute.value = route
//
//        }
//    }
//
//}