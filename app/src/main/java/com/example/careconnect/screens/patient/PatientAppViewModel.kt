package com.example.careconnect.screens.patient

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class PatientAppViewModel @Inject constructor(
    val authRepository: AuthRepository
): MainViewModel() {
}