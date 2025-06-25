package com.example.careconnect.screens.patient.home

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel responsible for managing data related to the home screen for patients.
 *
 * Fetches and exposes a list of doctors and upcoming appointments for the current user.
 *
 * @property doctorList A StateFlow providing a list of doctors to display (random 5 doctors).
 * @property upcomingAppointments A StateFlow providing the user's upcoming confirmed appointments (max 3).
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    private val appointmentRepository: AppointmentRepository,
    private val authRepository: AuthRepository
): MainViewModel() {
    private val _doctorList = MutableStateFlow<List<Doctor>>(emptyList())
    val doctorList: StateFlow<List<Doctor>> = _doctorList

    private val _upcomingAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val upcomingAppointments: StateFlow<List<Appointment>> = _upcomingAppointments

    init {
        fetchDoctors()
        fetchUpcomingAppointments()
    }

    /**
     * Fetches all doctors from the repository and selects a random subset for display.
     */
    private fun fetchDoctors() {
        viewModelScope.launch {
            val allDoctors = doctorRepository.getAllDoctors()
            _doctorList.value = allDoctors.shuffled().take(5) // Random 5 doctors
        }
    }

    /**
     * Fetches upcoming confirmed appointments for the current user, limited to the next 3 appointments.
     *
     * Handles cases where the user ID is null or errors occur during fetching by logging and returning empty list.
     */
    fun fetchUpcomingAppointments() {
        launchCatching {
            val userId = authRepository.currentUser?.uid
            println("DEBUG: User ID is $userId")
            if (userId == null) {
                println("ERROR: User ID is null when trying to load upcoming appointments")
                return@launchCatching
            }
            val today = LocalDate.now().toString()
            println("DEBUG: Fetching appointments for date $today and later")
            val upcomingAppointments = try {
                appointmentRepository.getPatientAppointmentsUpcoming(userId, today)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Return empty list on error
            }
            println("DEBUG: Found ${upcomingAppointments.size} upcoming appointments")
            _upcomingAppointments.value = upcomingAppointments.filter { appt -> // Start the filter, limit and sort
                appt.status == AppointmentStatus.CONFIRMED // only tale the confirmed one
            }.sortedWith ( // sort by date then start time
                compareBy<Appointment> { LocalDate.parse(it.appointmentDate) }
                    .thenBy { it.startTime }
            ).take(3) // Limit to 3

            println("DEBUG: Found ${upcomingAppointments.size} upcoming appointments, " +
                    "${_upcomingAppointments.value.size} after filtering")
        }
    }
}