package com.example.careconnect.screens.admin.home

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository,
    ): MainViewModel() {
    private val _appointmentsToday = MutableStateFlow(0)
    val appointmentsToday: StateFlow<Int> = _appointmentsToday

    private val _upcomingAppointments = MutableStateFlow(0)
    val upcomingAppointments: StateFlow<Int> = _upcomingAppointments

    private val _recentDoctors = MutableStateFlow<List<Doctor>>(emptyList())
    val recentDoctors: StateFlow<List<Doctor>> = _recentDoctors

    private val _doctorsWorkingToday = MutableStateFlow<List<Doctor>>(emptyList())
    val doctorsWorkingToday: StateFlow<List<Doctor>> = _doctorsWorkingToday

    private val _cancelledAppointmentsToday = MutableStateFlow(0)
    val cancelledAppointmentsToday: StateFlow<Int> = _cancelledAppointmentsToday

    private val _appointmentsUpcoming = MutableStateFlow<List<Appointment>>(emptyList())
    val appointmentsUpcoming: StateFlow<List<Appointment>> = _appointmentsUpcoming

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _appointmentsToday.value = appointmentRepository.getTodayAppointments().size
            _upcomingAppointments.value = appointmentRepository.getUpcomingAppointmentsToday().size
            _recentDoctors.value = doctorRepository.getRecentlyAddedDoctors()
            _doctorsWorkingToday.value = doctorRepository.getDoctorsWorkingToday()
            _cancelledAppointmentsToday.value = appointmentRepository.getCanceledAppointmentsToday().size
            _appointmentsUpcoming.value = appointmentRepository.getUpcomingAppointmentsToday()
        }
    }


}