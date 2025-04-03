package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AddDoctorRepository
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.dataclass.Role
import com.example.careconnect.screens.signup.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AddDoctorViewModel @Inject constructor(
    private val addDoctorRepository: AddDoctorRepository
): MainViewModel() {

    fun createDoctorInfo(
        name: String,
        surname: String,
        email: String,
        phone: String,
        address: String,
        specialization: String,
        experience: Int,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        if (!email.isValidEmail()) {
            showErrorSnackbar(ErrorMessage.IdError(R.string.invalid_email))
            return
        }

        launchCatching {
            val doctorInfo = Doctor(
                name = name,
                surname = surname,
                email = email,
                role = Role.DOCTOR,
                phone = phone, address = address, specialization = specialization, experience = experience)

            addDoctorRepository.createDoctor(doctorInfo)

        }
    }
}