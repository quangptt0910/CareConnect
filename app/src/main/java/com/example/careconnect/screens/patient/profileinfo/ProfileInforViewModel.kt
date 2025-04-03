package com.example.careconnect.screens.patient.profileinfo

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProfileInforViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {

    val userId: String?= authRepository.currentUser?.uid
    val currentUserId: Flow<String?> = authRepository.currentUserIdFlow

    fun linkAccount(
        gender: String,
        weight: Double,
        height: Double,
        dob: String,
        address: String
    ){
        launchCatching {
            userId?.let { userId ->
                authRepository.linkAccount(userId, weight, height, dob, address, gender)
            }
        }

    }
}