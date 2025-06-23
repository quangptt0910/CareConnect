package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.SnackBarMessage
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

    private val _navigateToDoctorSchedule = MutableStateFlow(false)
    val navigateToDoctorSchedule: StateFlow<Boolean>
        get() = _navigateToDoctorSchedule.asStateFlow()

    private val _newDoctorId = MutableStateFlow<String?>(null)
    val newDoctorId: StateFlow<String?>
        get() = _newDoctorId.asStateFlow()


    fun createDoctorInfo(
        name: String,
        surname: String,
        email: String,
        phone: String,
        address: String,
        specialization: String,
        experience: String,
        password: String, // Give some random password and they change later
        showSnackBar: (SnackBarMessage) -> Unit
    ) {
        if (name.isEmpty()) {
        showSnackBar(SnackBarMessage.IdMessage(R.string.name))
        return
        }
        if (surname.isEmpty()) {
        showSnackBar(SnackBarMessage.IdMessage(R.string.surname))
        return
        }
        if (email.isEmpty()) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.email))
            return
        }

        if (!email.isValidEmail()) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.invalid_email))
            return
        }

        if (experience.toIntOrNull() == null) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.experience))
            return
        }

        if (phone.toLongOrNull() == null) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.phone))
            return
        }

        launchCatching(showSnackBar) {
            val doctorDataMap = hashMapOf(
                "name" to name,
                "surname" to surname,
                "phone" to phone,
                "address" to address,
                "specialization" to specialization,
                "experience" to experience.toInt(),
                "profilePhoto" to "",
            )
            println("DEBUG:: Doctor data map: $doctorDataMap")
            val (message, doctorId) = doctorRepository.createDoctor(email = email, password = password, doctorData = doctorDataMap)
            _newDoctorId.value = doctorId
            println("DEBUG:: message $message")
            showSnackBar(SnackBarMessage.StringMessage(message))
            println("DEBUG:: PROFILE Doctor created successfully with Id: $doctorId")
            _navigateToDoctorSchedule.value = true
        }
    }
}