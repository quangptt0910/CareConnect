package com.example.careconnect.screens.doctor.home

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDate
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

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>>
        get() = _appointments.asStateFlow()

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

            val today = LocalDate.now().toString()
            _appointments.value = appointmentRepository.getDoctorAppointmentsUpcoming(
                authRepository.currentUser?.uid,
                today
            ).filter { appt -> // Start the filter, limit and sort
                appt.status == AppointmentStatus.CONFIRM // only tale the confirmed one
            }.sortedWith ( // sort by date then start time
                compareBy<Appointment> { LocalDate.parse(it.appointmentDate) }
                    .thenBy { it.startTime }
            ).take(3) // Limit to 3

        }
    }

    fun updateAppointmentStatus(appointment: Appointment, newStatus: AppointmentStatus) {
        launchCatching {
            val updatedAppointment = appointment.copy(status = newStatus)
            appointmentRepository.updateAppointment(updatedAppointment)

            loadPendingAppointments()
        }
    }
}
