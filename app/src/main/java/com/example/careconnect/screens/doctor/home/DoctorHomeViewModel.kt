package com.example.careconnect.screens.doctor.home

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        .mapLatest { patients ->
            patients.take(3)
        }

    private val _pendingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val pendingAppointments: StateFlow<List<Appointment>>
        get() = _pendingAppointments.asStateFlow()

    init {
        loadPendingAppointments()
    }

    fun loadPendingAppointments() {
        launchCatching {
            println("DEBUG: PendingAppt uid: ${authRepository.currentUser?.uid}")
            _pendingAppointments.value = appointmentRepository.getDoctorAppointmentsByStatus(
                authRepository.currentUser?.uid,
                AppointmentStatus.PENDING
            )
        }
    }
}

sealed interface PatientsUiState {
    object Loading : PatientsUiState
    data class Success(val patients: List<Patient>) : PatientsUiState
    data class Error(val message: String) : PatientsUiState
}