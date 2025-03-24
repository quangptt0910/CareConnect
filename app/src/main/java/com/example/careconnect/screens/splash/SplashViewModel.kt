package com.example.careconnect.screens.splash

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : MainViewModel() {

    private val _navigationRoute = MutableStateFlow<String?>(null)
    val navigationRoute = _navigationRoute.asStateFlow()

    init {
        launchCatching {
            val userData = authRepository.currentUserFlow.first()
            println("Debug Splash: $userData")
            val route = when(userData) {
                is AuthRemoteDataSource.UserData.AdminData -> "admin"
                is AuthRemoteDataSource.UserData.DoctorData -> "doctor"
                AuthRemoteDataSource.UserData.Error -> "login"
                AuthRemoteDataSource.UserData.NoUser -> "login"
                is AuthRemoteDataSource.UserData.PatientData -> "patient"
            }
            println("Debug Splash route: $route")
            _navigationRoute.value = route

        }
    }

}