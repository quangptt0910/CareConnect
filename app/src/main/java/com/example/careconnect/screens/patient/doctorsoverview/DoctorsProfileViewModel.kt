package com.example.careconnect.screens.patient.doctorsoverview

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DoctorsProfileViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    fun setDoctorId(doctorId: String) {
        launchCatching {
            _doctor.value = doctorRepository.getDoctorById(doctorId)
        }
    }
}