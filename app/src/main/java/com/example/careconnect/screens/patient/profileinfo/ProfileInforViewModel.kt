package com.example.careconnect.screens.patient.profileinfo

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileInforViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    init {

    }
    fun linkAccount(
        userId: String,
        gender: String,
        weight: Double,
        height: Double,
        dob: String,
        address: String
    ){
        launchCatching {
            authRepository.linkAccount(userId, weight, height, dob, address, gender)
        }

    }
}