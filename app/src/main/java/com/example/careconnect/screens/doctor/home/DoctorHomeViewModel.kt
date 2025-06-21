package com.example.careconnect.screens.doctor.home

import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.Task
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

    val tasks = doctorRepository.getTasksFlow(authRepository.currentUserIdFlow)
    val doctorId = authRepository.currentUser?.uid

    init {
        // Check if user is authenticated before loading data
        if ( doctorId != null) {
            loadPendingAppointments()
            loadUpcomingAppointments()
        } else {
            println("ERROR: User not authenticated, cannot load data")
        }
    }


    fun addTask(task: Task, showSnackBar: (SnackBarMessage) -> Unit) {
        if (doctorId.isNullOrBlank()) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.generic_error))
            return
        }

        if (task.name.isBlank()) {
            showSnackBar(SnackBarMessage.IdMessage(R.string.task_without_name))
            return
        }

        launchCatching {
            if (task.id.isBlank()) {
                println("DEBUG: Adding new task: $task")
                doctorRepository.addTask(doctorId, task)
            } else {
                println("DEBUG: Updating task: $task")
                doctorRepository.updateTask(doctorId, task)
            }
        }
    }

    fun deleteTask(task: Task) {
        launchCatching {
            if (task.id.isNotBlank()) doctorRepository.deleteTask(doctorId.toString(), task)
        }
    }

    fun updateTask(task: Task) {
        launchCatching {
            doctorRepository.updateTask(doctorId.toString(), task)
            println("DEBUG: Updated task: $task")
        }
    }

    fun loadPendingAppointments() {
        launchCatching {
            println("DEBUG: getting pending appointments for doctor")
            _pendingAppointments.value = appointmentRepository.getDoctorAppointmentsByStatus(
                authRepository.currentUser?.uid,
                AppointmentStatus.PENDING
            )
            println("DEBUG: PENDING-Found ${_pendingAppointments.value.size} pending appointments")
        }
    }

    fun loadUpcomingAppointments() {
        launchCatching {
            val userId = authRepository.currentUser?.uid
            if (userId == null) {
                println("ERROR: User ID is null when trying to load upcoming appointments")
                return@launchCatching
            }
            val today = LocalDate.now().toString()
            println("DEBUG: Fetching appointments for date $today and later")
            val upcomingAppointments = try {
                appointmentRepository.getDoctorAppointmentsUpcoming(userId, today)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Return empty list on error
            }
            println("DEBUG: Found ${upcomingAppointments.size} upcoming appointments")
            _appointments.value = upcomingAppointments.filter { appt -> // Start the filter, limit and sort
                appt.status == AppointmentStatus.CONFIRMED // only tale the confirmed one
            }.sortedWith ( // sort by date then start time
                compareBy<Appointment> { LocalDate.parse(it.appointmentDate) }
                    .thenBy { it.startTime }
            ).take(3) // Limit to 3

            println("DEBUG: Found ${upcomingAppointments.size} upcoming appointments, " +
                    "${_appointments.value.size} after filtering")
        }
    }

    fun updateAppointmentStatus(appointment: Appointment, newStatus: AppointmentStatus) {
        launchCatching {
            val updatedAppointment = appointment.copy(status = newStatus)
            appointmentRepository.updateAppointment(updatedAppointment)

            loadPendingAppointments()
            loadUpcomingAppointments()
        }
    }
}
