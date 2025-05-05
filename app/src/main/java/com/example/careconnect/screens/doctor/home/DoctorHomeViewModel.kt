package com.example.careconnect.screens.doctor.home

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject


@HiltViewModel
class DoctorHomeViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val authRepository: AuthRepository,
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val patientList = doctorRepository.getPatientsList(authRepository.currentUserIdFlow)
        .mapLatest {patients ->
            patients.take(3)
        }

}

sealed interface PatientsUiState {
    object Loading : PatientsUiState
    data class Success(val patients: List<Patient>) : PatientsUiState
    data class Error(val message: String) : PatientsUiState
}