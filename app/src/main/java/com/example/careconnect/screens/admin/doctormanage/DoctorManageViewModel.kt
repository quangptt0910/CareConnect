package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DoctorManageViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    fun createDoctor(doctor: Doctor) {
        launchCatching {
            doctorRepository.createDoctor(doctor)
        }
    }
    fun updateDoctor(doctor: Doctor) {
        launchCatching {
            doctorRepository.updateDoctor(doctor)
        }
    }

    fun signupDoctor(email: String, password: String) {
        launchCatching {
            doctorRepository.signupDoctor(email, password)
        }
    }

}