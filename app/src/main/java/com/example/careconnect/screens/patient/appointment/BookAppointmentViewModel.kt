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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * UI state data class for managing the book appointment screen.
 *
 * @property selectedDate Currently selected date for the appointment. Defaults to today.
 * @property selectedTimeSlot Currently selected time slot for the appointment, or null if none selected.
 * @property availableSlots List of available time slots for the selected date.
 * @property isLoading Flag indicating whether data is currently loading (e.g., slots or doctor info).
 * @property isBooking Flag indicating whether an appointment booking process is ongoing.
 */
data class BookAppointmentUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedTimeSlot: TimeSlot? = null,
    val availableSlots: List<TimeSlot> = emptyList(),
    val isLoading: Boolean = false,
    val isBooking: Boolean = false
)

/**
 * ViewModel responsible for managing the state and business logic of the book appointment screen.
 *
 * This ViewModel handles:
 * - Fetching doctor information by ID
 * - Loading and filtering available appointment time slots by selected date
 * - Managing selected date and time slot state
 * - Validating user selections (e.g., no past dates or times)
 * - Booking appointments with backend repositories
 * - Providing UI state via a StateFlow for reactive UI updates
 *
 * @property appointmentRepository Repository for creating and managing appointments.
 * @property doctorRepository Repository for fetching doctor data and available slots.
 * @property authRepository Repository to get current authenticated user.
 * @property patientRepository Repository to fetch patient details.
 */
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

    /**
     * Sets the current doctor ID and triggers loading of doctor info and available slots.
     *
     * @param doctorId The unique ID of the doctor to load data for.
     */
    fun setDoctorId(doctorId: String) {
        if (_doctorId.value != doctorId) {
            _doctorId.value = doctorId
            loadDoctorInfo()
            loadAvailableSlots {  }
        }
    }

    /**
     * Loads detailed information of the currently selected doctor from repository.
     */
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

    /**
     * Handles user date selection with validation to prevent past dates.
     *
     * @param date The date selected by the user.
     * @param showSnackBar Function to display error messages to the user.
     */
    fun onDateSelected(date: LocalDate, showSnackBar: (SnackBarMessage) -> Unit) {
        // Validate that the selected date is not in the past
        if (date.isBefore(LocalDate.now())) {
            showSnackBar(SnackBarMessage.StringMessage("Cannot select past dates"))
            return
        }
        _selectedDate.value = date
        _selectedTimeSlot.value = null
        loadAvailableSlots(showSnackBar)
    }

    /**
     * Handles user time slot selection with validation to prevent past time slots on the current day.
     *
     * @param timeSlot The time slot selected by the user.
     */
    fun onTimeSelected(timeSlot: TimeSlot) {
        // Additional validation to ensure the time slot is not in the past
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()
        val selectedDate = _selectedDate.value

        if (selectedDate == currentDate) {
            try {
                val slotTime = parseTimeString(timeSlot.startTime)
                if (slotTime != null && slotTime.isBefore(currentTime)) {
                    // Don't allow selection of past time slots
                    return
                }
            } catch (e: Exception) {
                println("Error parsing time: ${timeSlot.startTime}")
                return
            }
        }

        _selectedTimeSlot.value = timeSlot
    }

    /**
     * Parses a time string into a LocalTime object, supporting multiple formats.
     *
     * @param timeString Time string to parse (e.g., "09:00" or "9:00").
     * @return Parsed LocalTime object, or null if parsing failed.
     */
    private fun parseTimeString(timeString: String): LocalTime? {
        return try {
            // First try with HH:mm format (e.g., "09:00")
            LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            try {
                // Then try with H:mm format (e.g., "9:00")
                LocalTime.parse(timeString, DateTimeFormatter.ofPattern("H:mm"))
            } catch (e2: Exception) {
                println("Error parsing time: $timeString")
                null
            }
        }
    }

    /**
     * Initiates the booking process for the selected appointment slot with validations.
     *
     * @param showSnackBar Function to display success or error messages to the user.
     */
    fun bookAppointment(showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar) {
            try {
                _isLoading.value = true
                val currentState = uiState.value
                val currentDoctor = _doctor.value
                val currentPatient = patientRepository.getPatientById(authRepository.getCurrentUserId() ?: "")
                val selectedSlot = currentState.selectedTimeSlot

                // Validate selected date is not in the past
                if (currentState.selectedDate.isBefore(LocalDate.now())) {
                    showSnackBar(SnackBarMessage.StringMessage("Cannot book appointments for past dates"))
                    return@launchCatching
                }

                if (currentState.selectedDate == LocalDate.now()) {
                    val slotTime = parseTimeString(selectedSlot?.startTime ?: "" )
                    if (slotTime != null && slotTime.isBefore(LocalTime.now())) {
                        showSnackBar(SnackBarMessage.StringMessage("Cannot book appointments for past time slots"))
                        return@launchCatching
                    } else if (slotTime == null) {
                        showSnackBar(SnackBarMessage.StringMessage("Invalid time format"))
                        return@launchCatching
                    }
                }

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

    /**
     * Loads the available time slots for the currently selected doctor and date,
     * filtering out past slots for the current day.
     *
     * @param showSnackBar Function to display error messages if loading fails.
     */
    private fun loadAvailableSlots(showSnackBar: (SnackBarMessage) -> Unit) {
        launchCatching(showSnackBar) {
            _isLoading.value = true
            try {
                val slots = doctorRepository.getAvailableSlots(
                    doctorId = _doctorId.value ?: "",
                    date = _selectedDate.value
                )

                // Filter out past time slots for current date
                val filteredSlots = if (_selectedDate.value == LocalDate.now()) {
                    val currentTime = LocalTime.now()
                    slots.filter { slot ->
                        val slotTime = parseTimeString(slot.startTime)
                        if (slotTime != null) {
                            !slotTime.isBefore(currentTime)
                        } else {
                            // If parsing fails, include the slot but log the error
                            println("DEBUG Error parsing time for slot: ${slot.startTime}")
                            true
                        }
                    }
                } else {
                    slots
                }

                _availableSlots.value = filteredSlots
                println("DEBUG: loadAvailableSlots ${_availableSlots.value}")
            } catch (e: Exception) {
                showSnackBar(SnackBarMessage.StringMessage("Failed to load available slots"))
                _availableSlots.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Refreshes the available slots, typically after a booking is completed.
     *
     * @param showSnackBar Function to display error messages if refresh fails.
     */
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