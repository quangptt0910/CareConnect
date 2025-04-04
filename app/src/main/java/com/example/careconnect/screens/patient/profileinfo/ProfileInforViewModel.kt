package com.example.careconnect.screens.patient.profileinfo

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileInforViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()


    val userId: String?= authRepository.currentUser?.uid

    fun linkAccount(
        gender: String,
        weight: Double,
        height: Double,
        dob: String,
        address: String,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ){
        launchCatching {
            val uid = userId ?: authRepository.currentUser?.uid
            if (uid == null) {
                showErrorSnackbar(ErrorMessage.IdError(R.string.generic_error))
            } else {
                authRepository.linkAccount(uid, gender, weight, height, dob, address)
                _shouldRestartApp.value = true

            }
        }

    }
}