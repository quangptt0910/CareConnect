package com.example.careconnect.screens.patient.appointment

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

/**
 * Enum representing the time range selection for filtering patient appointments.
 *
 * - [Month]: Filter appointments by a specific month.
 * - [All]: Show all appointments without date restriction.
 */
enum class PatientTimeRange { Month, All }

/**
 * Enum representing sorting options available for patient appointments.
 *
 * @property label A user-friendly label describing the sorting option.
 */
enum class PatientSortOption(val label: String) {
    TimeAsc("Time: Earliest"),
    TimeDesc("Time: Latest"),
    DoctorName("Doctor Name"),
    Status("Status")
}

/**
 * Data class representing the UI state for the Patient Appointment screen.
 *
 * @property patient The patient whose appointments are displayed.
 * @property selectedRange The currently selected time range filter.
 * @property appointments List of appointments after applying filters and sorting.
 * @property isLoading Flag indicating if data is currently being loaded.
 * @property error Optional error message to display in case of data fetch failure.
 * @property currentDate The current date used for filtering when [PatientTimeRange.Month] is selected.
 * @property filterStatus Set of appointment statuses used to filter appointments.
 * @property sortOption The selected sort option for ordering appointments.
 */
data class PatientAppointmentUiState(
    val patient: Patient? = null,

    val selectedRange: PatientTimeRange = PatientTimeRange.All,
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,

    val currentDate: LocalDate = LocalDate.now(),
    val filterStatus: Set<AppointmentStatus?> = emptySet(),
    val sortOption: PatientSortOption = PatientSortOption.TimeDesc
)

/**
 * ViewModel for the Patient Appointment screen.
 *
 * Responsible for loading patient data and their appointments, managing UI state related to:
 * - Time range selection (monthly/all)
 * - Date navigation within month view
 * - Appointment status filtering
 * - Sorting of appointments
 * - Resetting filters and sorting to defaults
 *
 * It combines multiple state flows representing these controls and fetches appointment data
 * accordingly from the [AppointmentRepository].
 *
 * The ViewModel also fetches the currently logged-in patient using [AuthRepository] and
 * retrieves patient details from [PatientRepository].
 *
 * @constructor Injects required repositories for patient, appointment, and authentication data.
 *
 * @property appointmentRepository Repository providing appointment data operations.
 * @property patientRepository Repository providing patient data operations.
 * @property authRepository Repository providing authentication-related operations.
 */
@HiltViewModel
class PatientAppointmentViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val authRepository: AuthRepository
) : MainViewModel() {

    // Get patient ID from navigation args
    private val patientId = authRepository.getCurrentUserId().toString()

    // Patient info
    private val _patient = MutableStateFlow<Patient?>(null)

    private val isoFormatter = DateTimeFormatter.ISO_DATE
    private val weekFields = WeekFields.of(Locale.getDefault())

    // Controls
    private val _selectedRange = MutableStateFlow(PatientTimeRange.All)
    private val _currentDate   = MutableStateFlow(LocalDate.now())
    private val _filterStatus  = MutableStateFlow<Set<AppointmentStatus?>>(emptySet())
    private val _sortOption    = MutableStateFlow(PatientSortOption.TimeAsc)

    init {
        loadPatient()
    }

    /**
     * Loads the patient details asynchronously and updates [_patient].
     */
    private fun loadPatient() {
        launchCatching {
            val patient = patientRepository.getPatientById(patientId)
            _patient.value = patient
        }
    }

    /**
     * Combines patient data, selected filters, date, and sort options into a single [StateFlow]
     * of [PatientAppointmentUiState] that the UI can observe.
     *
     * This flow also fetches the appointment list from [AppointmentRepository] according to
     * the selected time range, applies filtering by status and sorting,
     * then emits the updated UI state.
     */
    // Combined UI state
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PatientAppointmentUiState> = combine(
        _patient,
        _selectedRange,
        _currentDate,
        _filterStatus,
        _sortOption
    ) { patient, range, date, status, sort ->
        PatientAppointmentUiState(
            patient = patient,
            selectedRange = range,
            currentDate = date,
            filterStatus = status,
            sortOption = sort,
            isLoading = true
        )
    }
        .flatMapLatest { state ->
            // fetch raw list based on range and patientId
            val rawFlow: Flow<List<Appointment>> = flow {
                if (state.patient == null) {
                    emit(emptyList())
                    return@flow
                }

                val list = when(state.selectedRange) {
                    PatientTimeRange.Month -> {
                        val ym = YearMonth.from(state.currentDate)
                        appointmentRepository.getPatientAppointmentsByMonth(
                            patientId,
                            ym.atDay(1).format(isoFormatter)
                        )
                    }
                    PatientTimeRange.All -> appointmentRepository.getAllPatientAppointments(patientId)
                }
                emit(list)
            }

            // apply filter and sort
            rawFlow.map { list ->
                list.filter { state.filterStatus.isEmpty() || it.status in state.filterStatus }
                    .let { filtered ->
                        when(state.sortOption) {
                            PatientSortOption.TimeAsc -> filtered.sortedBy { it.appointmentDate }
                            PatientSortOption.TimeDesc -> filtered.sortedByDescending { it.appointmentDate }
                            PatientSortOption.DoctorName -> filtered.sortedBy { it.doctorName }
                            PatientSortOption.Status -> filtered.sortedBy { it.status.value }
                        }
                    }
            }
                .map { sortedList ->
                    PatientAppointmentUiState(
                        patient = state.patient,
                        appointments = sortedList,
                        isLoading = false,
                        selectedRange = state.selectedRange,
                        currentDate = state.currentDate,
                        filterStatus = state.filterStatus,
                        sortOption = state.sortOption
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, PatientAppointmentUiState())

    // UI event handlers
    /**
     * Updates the selected time range filter.
     *
     * @param range The new time range to select.
     */
    fun setRange(range: PatientTimeRange) { _selectedRange.value = range }

    /**
     * Updates the current date (used for month navigation).
     *
     * @param date The new date to set.
     */
    fun setDate(date: LocalDate) { _currentDate.value = date }

    /**
     * Updates the set of appointment statuses used to filter the list.
     *
     * @param status The new set of statuses to filter by.
     */
    fun setFilter(status: Set<AppointmentStatus?>) { _filterStatus.value = status }

    /**
     * Updates the sorting option applied to the appointment list.
     *
     * @param option The new sort option.
     */
    fun setSort(option: PatientSortOption) { _sortOption.value = option }

    /**
     * Resets all filters, sort option, and date to their default values.
     */
    fun resetAll() {
        _selectedRange.value = PatientTimeRange.All
        _filterStatus.value = emptySet()
        _sortOption.value = PatientSortOption.TimeAsc
        _currentDate.value = LocalDate.now()
    }

}