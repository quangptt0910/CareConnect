package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel responsible for managing the doctor data and business logic
 * in the Doctor Manage screen.
 *
 * Provides flows for doctor list and navigation state, and handles data loading.
 *
 * @property doctorRepository Repository for doctor data operations.
 */
@HiltViewModel
class DoctorManageViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _workingDays = MutableStateFlow<Set<LocalDate>>(emptySet())
    val workingDays: StateFlow<Set<LocalDate>> = _workingDays

    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    private val _navigateToAddDoctor = MutableStateFlow(false)
    val navigateToAddDoctor: StateFlow<Boolean>
        get() = _navigateToAddDoctor.asStateFlow()

    private val _doctorsList = MutableStateFlow<List<Doctor>>(emptyList())
    val doctorsList: StateFlow<List<Doctor>>
        get() = _doctorsList.asStateFlow()

//    private val _isLoadingDoctors = MutableStateFlow(true)
//    val isLoadingDoctors: StateFlow<Boolean>
//        get() = _isLoadingDoctors.asStateFlow()

    val allDoctors = doctorRepository.getAllDoctorsFlow()

    /**
     * Loads the list of doctors asynchronously and updates the internal state.
     */
    fun loadDoctors() {
        launchCatching {
            _doctorsList.value = doctorRepository.getAllDoctors()
        }
    }

    suspend fun deleteDoctor(doctor: Doctor) {
        doctorRepository.deleteDoctor(doctor)
    }

    fun updateDoctor(updateDoctor: Doctor) {
        _doctor.value = updateDoctor
        launchCatching {
            doctorRepository.updateDoctor(updateDoctor)
        }
    }

    suspend fun getDoctorScheduleDates(doctorId: String): Set<LocalDate> =
        doctorRepository.getWorkingDays(doctorId)
            .first() // collect the current set once
            .toSet()

    fun observeWorkingDays(doctorId: String) = launchCatching {
        doctorRepository.getWorkingDays(doctorId)
            .collect { days -> _workingDays.value = days }
    }

    fun updateDoctorSchedule(doctorId: String, dates: Set<LocalDate>) {
        launchCatching {
            doctorRepository.saveWorkingDays(doctorId, dates)
        }
    }


}