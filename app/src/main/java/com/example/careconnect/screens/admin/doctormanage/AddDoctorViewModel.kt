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

/**
 * ViewModel responsible for adding a new doctor.
 *
 * Manages form validation, doctor creation via repository, and navigation triggers.
 *
 * @property doctorRepository Repository managing doctor data operations.
 * @property authRepository Repository managing authentication operations.
 */
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

    /**
     * Validates and creates a new doctor record.
     *
     * Performs field validations such as empty checks, email format, and numeric conversions.
     * Calls the doctor repository to create the doctor, updates state flows, and displays snack bar messages.
     *
     * @param name Doctor's first name.
     * @param surname Doctor's surname.
     * @param email Doctor's email address.
     * @param phone Doctor's phone number as string.
     * @param address Doctor's address.
     * @param specialization Doctor's medical specialization.
     * @param experience Doctor's years of experience as string.
     * @param password Password for doctor's account.
     * @param showSnackBar Callback to show snack bar messages.
     */
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