package com.example.careconnect.screens.patient.appointment

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.R
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.dataclass.TimeSlot
import com.example.careconnect.dataclass.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject


data class BookAppointmentUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTimeSlot: TimeSlot? = null,
    val availableSlots: List<TimeSlot> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class BookAppointmentViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository,
    private val patientRepository: PatientRepository
) : MainViewModel() {

    // UI State handling
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _selectedTimeSlot = MutableStateFlow<TimeSlot?>(null)
    private val _availableSlots = MutableStateFlow<List<TimeSlot>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    private val _doctorId = MutableStateFlow<String?>(null)
    private val _doctor = MutableStateFlow<Doctor?>(null)
    val doctor: StateFlow<Doctor?> = _doctor

    fun setDoctorId(doctorId: String) {
        if (_doctorId.value != doctorId) {
            _doctorId.value = doctorId
            loadDoctorInfo()
            loadAvailableSlots {  }
        }
    }

    private fun loadDoctorInfo() {
        val doctorId = _doctorId.value ?: return
        launchCatching {
            try {
                _isLoading.value = true
                _doctor.value = doctorRepository.getDoctorById(doctorId)
            } catch (e: Exception) {

                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    val uiState: StateFlow<BookAppointmentUiState> = combine(
        _selectedDate,
        _selectedTimeSlot,
        _availableSlots,
        _isLoading
    ) { selectedDate, selectedTimeSlot, availableSlots, isLoading ->
        BookAppointmentUiState(
            selectedDate = selectedDate,
            selectedTimeSlot = selectedTimeSlot,
            availableSlots = availableSlots,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BookAppointmentUiState()
    )

    fun onDateSelected(date: LocalDate, showSnackBar: (SnackBarMessage) -> Unit) {
        _selectedDate.value = date
        _selectedTimeSlot.value = null
        loadAvailableSlots(showSnackBar)
    }

    fun onTimeSelected(timeSlot: TimeSlot) {
        _selectedTimeSlot.value = timeSlot
    }


    fun bookAppointment(showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar) {
            try {
                _isLoading.value = true
                val currentState = uiState.value
                val currentDoctor = _doctor.value
                val currentPatient = patientRepository.getPatientById(authRepository.getCurrentUserId() ?: "")
                val selectedSlot = currentState.selectedTimeSlot

                if (currentState.selectedTimeSlot == null) {
                    showSnackBar(SnackBarMessage.IdMessage(R.string.please_select_time))
                    return@launchCatching
                }

                if (currentDoctor == null) {
                    showSnackBar(SnackBarMessage.StringMessage("Doctor information not available"))
                    return@launchCatching
                }

                val appointment = Appointment(
                    patientId = currentPatient?.id ?: "",
                    doctorId = _doctorId.value ?: "",
                    patientName = "${currentPatient?.name}",
                    doctorName = currentDoctor.name,
                    type = selectedSlot.slotType.name,
                    appointmentDate = currentState.selectedDate.toDateString(),
                    startTime = selectedSlot.startTime,
                    endTime = selectedSlot.endTime,
                    address = currentDoctor.address,
                    status = AppointmentStatus.PENDING
                )

//                appointmentRepository.createAppointment(appointment)
                appointmentRepository.createAppointmentWithSlotUpdate(
                    appointment = appointment,
                    doctorId = _doctorId.value ?: "",
                    date = currentState.selectedDate.toDateString(),
                    targetTimeSlot = selectedSlot
                )
                showSnackBar(SnackBarMessage.IdMessage(R.string.appointment_booked_success))
                refreshSlots(showSnackBar)
            } catch (e: Exception) {
                showSnackBar(SnackBarMessage.StringMessage(e.message ?: "Failed to book appointment"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadAvailableSlots(showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar) {
            _isLoading.value = true
            try {
                _availableSlots.value = doctorRepository.getAvailableSlots(
                    doctorId = _doctorId.value ?: "",
                    date = _selectedDate.value
                )

                println("DEBUG: loadAvailableSlots ${_availableSlots.value}")
            } catch (e: Exception) {
                showSnackBar(SnackBarMessage.StringMessage("Failed to load available slots"))
                _availableSlots.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun refreshSlots(showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar){
            try {
                // Force refresh from network
                // doctorRepository.clearCache(doctorId = _doctorId.value ?: "")
                loadAvailableSlots(showSnackBar)
            } catch (e: Exception) {
                showSnackBar(SnackBarMessage.StringMessage(
                    "Failed to refresh availability"
                ))
            }
        }
    }

}