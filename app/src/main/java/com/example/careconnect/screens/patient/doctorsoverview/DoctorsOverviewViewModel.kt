package com.example.careconnect.screens.patient.doctorsoverview


import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Doctor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


/**
 * ViewModel responsible for managing the state and business logic
 * related to the list of doctors filtered by specialty.
 *
 * @property doctorRepository Repository to fetch doctor data.
 */
@HiltViewModel
class DoctorsOverviewViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _specialty = MutableStateFlow("")
    val specialty: StateFlow<String> = _specialty.asStateFlow()

    private val _doctors = MutableStateFlow<List<Doctor>>(emptyList())
    val doctors: StateFlow<List<Doctor>> = _doctors.asStateFlow()

    /**
     * Updates the selected specialty and fetches doctors for that specialty.
     *
     * @param specialty The medical specialty to filter doctors by.
     */
    fun setSpecialty(specialty: String) {
        _specialty.value = specialty
        fetchDoctors(specialty)
    }

    /**
     * Fetches all doctors and filters them by the given specialty.
     *
     * @param specialty The specialty to filter doctors by.
     */
    private fun fetchDoctors(specialty: String) {
        launchCatching {
            val allDoctors = doctorRepository.getAllDoctors()
            _doctors.value = allDoctors.filter {
                it.specialization.equals(specialty, ignoreCase = true)
            }
        }
    }
}