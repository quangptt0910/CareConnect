package com.example.careconnect.screens.doctor

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the DoctorApp composable.
 *
 * Provides authentication data and any shared logic needed by the Doctor section screens.
 *
 * @property authRepository Repository to access authentication and user-related data.
 */
@HiltViewModel
class DoctorAppViewModel @Inject constructor(
    val authRepository: AuthRepository
): MainViewModel() {
}