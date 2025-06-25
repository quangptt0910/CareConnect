package com.example.careconnect.screens.doctor.patients

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject


/**
 * ViewModel responsible for providing the list of patients assigned to the current doctor.
 *
 * Uses the [DoctorRepository] and [AuthRepository] to fetch patient data.
 *
 * @property appointmentRepository Repository to access appointment data (not used directly here but may be extended).
 * @property authRepository Repository to get the current authenticated user ID.
 * @property doctorRepository Repository to get the doctor-specific data, including the patients list.
 */
@HiltViewModel
class PatientsViewModel @Inject constructor(
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