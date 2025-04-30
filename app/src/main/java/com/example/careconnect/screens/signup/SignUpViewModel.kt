package com.example.careconnect.screens.signup

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.dataclass.SnackBarMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _navigateToProfile = MutableStateFlow(false)
    val navigateToProfile: StateFlow<Boolean>
        get() = _navigateToProfile.asStateFlow()

    fun signUp(
        name: String,
        surname: String,
        email: String,
        password: String,
        showSnackbar: (SnackBarMessage) -> Unit
    ) {

        if (name.isBlank() || surname.isBlank() || email.isBlank() || password.isBlank()) {
            showSnackbar(SnackBarMessage.IdMessage(R.string.all_fields_required))
            return
        }

        if (!email.isValidEmail()) {
            showSnackbar(SnackBarMessage.IdMessage(R.string.invalid_email))
            return
        }
        if (!password.isValidPassword()) {
            showSnackbar(SnackBarMessage.IdMessage(R.string.invalid_password))
            return
        }

        launchCatching(showSnackbar) {
            println("Debug: SignUp clicked")
            authRepository.signUp(name, surname, email, password)
            println("Debug: SignUp success")
            _navigateToProfile.value = true

        }
    }
}

