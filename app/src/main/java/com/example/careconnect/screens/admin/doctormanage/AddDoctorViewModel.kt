package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.dataclass.Role
import com.example.careconnect.screens.signup.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class AddDoctorViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository
): MainViewModel() {

    private val _navigateToDoctorManage= MutableStateFlow(false)
    val navigateToDoctorManage: StateFlow<Boolean>
        get() = _navigateToDoctorManage.asStateFlow()

    val adminId = authRepository.currentUser?.uid

    fun createDoctorInfo(
        name: String,
        surname: String,
        email: String,
        phone: String,
        address: String,
        specialization: String,
        experience: String,
        password: String, // Give some random password and they change later
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        if (!email.isValidEmail()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.invalid_email))
            return
        }

        if (name.isEmpty()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.name))
            return
        }

        if (experience.toIntOrNull() == null) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.experience))
        }

        if (phone.toLongOrNull() == null) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.phone))
        }

        launchCatching {
            val doctorInfo = Doctor(
                name = name,
                surname = surname,
                email = email,
                role = Role.DOCTOR,
                phone = phone, address = address, specialization = specialization, experience = experience.toInt())
            doctorRepository.createDoctor(email = email, password = password, doctor = doctorInfo)
            println("DEBUG:: PROFILE Doctor created successfully!!")
            println("DEBUG:: Doctor created successfully!!")
            _navigateToDoctorManage.value = true
        }
    }
}