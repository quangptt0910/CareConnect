package com.example.careconnect.screens.doctor

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoctorAppViewModel @Inject constructor(
    val authRepository: AuthRepository
): MainViewModel() {
}