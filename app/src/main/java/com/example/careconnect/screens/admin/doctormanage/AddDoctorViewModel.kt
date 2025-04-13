package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
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
        if (name.isEmpty()) {
        showErrorSnackbar(ErrorMessage.IdError(R.string.name))
        return
        }
        if (surname.isEmpty()) {
        showErrorSnackbar(ErrorMessage.IdError(R.string.surname))
        return
        }
        if (email.isEmpty()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.email))
            return
        }

        if (!email.isValidEmail()) {
            println("DEBUG:: Invalid email")
            showErrorSnackbar(ErrorMessage.IdError(R.string.invalid_email))
            return
        }

        if (experience.toIntOrNull() == null) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.experience))
            return
        }

        if (phone.toLongOrNull() == null) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.phone))
            return
        }
        val scheduleMap = mapOf(
            "availability" to emptyList<Map<String, Any>>()   // empty list for availability
        )

        launchCatching(showErrorSnackbar) {
            val doctorDataMap = mapOf(
                "name" to name,
                "surname" to surname,
                "email" to email,
                "role" to Role.DOCTOR.name,  // Send as string; your cloud function can decide how to parse it
                "phone" to phone,
                "address" to address,
                "specialization" to specialization,
                "experience" to experience.toInt(),
                "profilePhoto" to "",
                "schedule" to scheduleMap
            )
            doctorRepository.createDoctor(email = email, password = password, doctorData = doctorDataMap)
            println("DEBUG:: PROFILE Doctor created successfully!!")
            println("DEBUG:: Doctor created successfully!!")
            _navigateToDoctorManage.value = true
        }
    }
}