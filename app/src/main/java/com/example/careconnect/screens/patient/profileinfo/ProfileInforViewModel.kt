package com.example.careconnect.screens.patient.profileinfo

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileInforViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    init {
        launchCatching {
            val currentUser = FirebaseAuth.getInstance().currentUser
            _userId.value = currentUser?.uid
        }
    }
    fun linkAccount(
        gender: String,
        weight: Double,
        height: Double,
        dob: String,
        address: String
    ){
        launchCatching {
            val currentUserId = _userId.value
            if (currentUserId != null) {
                authRepository.linkAccount(currentUserId, weight, height, dob, address, gender)
            }
        }

    }
}