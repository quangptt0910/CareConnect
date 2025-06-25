package com.example.careconnect.screens.patient.profileinfo

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.SnackBarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel responsible for managing profile information state and business logic.
 * Handles account linking via [AuthRepository] and triggers app restart if needed.
 *
 * @property authRepository Repository to handle authentication and user account operations.
 */
@HiltViewModel
class ProfileInfoViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    val userId: String? = authRepository.currentUser?.uid

    /**
     * Attempts to link the user's account with provided profile information.
     * Shows snackbar messages on failure.
     *
     * @param gender The user's gender.
     * @param weight The user's weight.
     * @param height The user's height.
     * @param dob The user's date of birth as a formatted string.
     * @param address The user's address.
     * @param showSnackBar Callback to display snackbar messages.
     */
    fun linkAccount(
        gender: String,
        weight: Double,
        height: Double,
        dob: String,
        address: String,
        showSnackBar: (SnackBarMessage) -> Unit
    ){
        launchCatching(showSnackBar) {
            val uid = userId ?: authRepository.currentUser?.uid
            if (uid == null) {
                showSnackBar(SnackBarMessage.IdMessage(R.string.generic_error))
            } else {
                authRepository.linkAccount(uid, gender, weight, height, dob, address)
                _shouldRestartApp.value = true

            }
        }

    }
}