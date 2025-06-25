package com.example.careconnect.screens.patient

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel responsible for managing the state and business logic for the Patient section of the app.
 *
 * This ViewModel provides access to authentication-related data through [AuthRepository]
 * and inherits common functionality from [MainViewModel].
 *
 * @property authRepository Repository to access authentication data and operations.
 *
 * @constructor Creates an instance of [PatientAppViewModel] with the provided [AuthRepository].
 */
@HiltViewModel
class PatientAppViewModel @Inject constructor(
    val authRepository: AuthRepository
): MainViewModel() {
}