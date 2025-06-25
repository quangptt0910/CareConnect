package com.example.careconnect.screens.doctor.appointments

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.DoctorRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
import com.example.careconnect.dataclass.Doctor
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
 * Enum representing the time range options for filtering appointments.
 */
enum class TimeRange { Day, Week, Month, All }

/**
 * Enum representing the sorting options for doctor appointments.
 * @property label The display label for the sort option.
 */
enum class DoctorSortOption(val label: String) {
    TimeAsc("Time: Earliest"),
    TimeDesc("Time: Latest"),
    PatientName("Patient Name"),
    Status("Status")
    //  TODO() implement to search for patients name
}


/**
 * Data class representing the UI state for the Doctor Appointment screen.
 *
 * @property doctor The current doctor's details.
 * @property selectedRange The currently selected time range for filtering appointments.
 * @property appointments The list of appointments to display.
 * @property isLoading True if data is currently being loaded, false otherwise.
 * @property error An error message if an error occurred, null otherwise.
 * @property currentDate The currently selected date for filtering.
 * @property filterStatus The set of appointment statuses to filter by.
 * @property sortOption The currently selected sorting option for appointments.
 */
data class DoctorAppointmentUiState(
    val doctor: Doctor? = null,

    val selectedRange: TimeRange =TimeRange.All,
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,

    val currentDate: LocalDate = LocalDate.now(),
    val filterStatus: Set<AppointmentStatus?> = emptySet(),
    val sortOption: DoctorSortOption = DoctorSortOption.TimeDesc
)


/**
 * ViewModel for the Doctor Appointment screen.
 *
 * This ViewModel is responsible for fetching and managing the data related to a doctor's appointments.
 * It provides a [StateFlow] of [DoctorAppointmentUiState] that the UI can observe to react to data changes.
 *
 * @param appointmentRepository Repository for accessing appointment data.
 * @param doctorRepository Repository for accessing doctor data.
 * @param authRepository Repository for accessing authentication data.
 */
@HiltViewModel
class DoctorAppointmentViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val doctorRepository: DoctorRepository,
    private val authRepository: AuthRepository
) : MainViewModel() {

    // Get doctor ID from navigation args
    private val doctorId = authRepository.getCurrentUserId().toString()
    private val _doctor = MutableStateFlow<Doctor?>(null)

    private val isoFormatter = DateTimeFormatter.ISO_DATE
    private val weekFields = WeekFields.of(Locale.getDefault())

    // Controls
    private val _selectedRange = MutableStateFlow(TimeRange.All)
    private val _currentDate   = MutableStateFlow(LocalDate.now())
    private val _filterStatus  = MutableStateFlow<Set<AppointmentStatus?>>(emptySet())
    private val _sortOption    = MutableStateFlow(DoctorSortOption.TimeAsc)

    init {
        loadDoctor()
    }

    private fun loadDoctor() {
        launchCatching {
            val doctor = doctorRepository.getDoctorById(doctorId)
            _doctor.value = doctor
        }
    }


    /**
     * A [StateFlow] representing the current UI state of the Doctor Appointment screen.
     *
     * It combines various internal state flows ([_doctor], [_selectedRange], [_currentDate], [_filterStatus], [_sortOption])
     * to produce a comprehensive [DoctorAppointmentUiState].
     *
     * The flow starts by emitting an initial loading state.
     * It then flatMaps to a flow that fetches raw appointments based on the selected range and doctor ID.
     * This raw list is then filtered and sorted based on the current filter and sort options.
     * Finally, it maps the sorted list to a new [DoctorAppointmentUiState] with `isLoading` set to false.
     *
     * The resulting flow is shared in the [viewModelScope] and starts lazily.
     */
    // Combined UI state
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DoctorAppointmentUiState> = combine(
        _doctor,
        _selectedRange,
        _currentDate,
        _filterStatus,
        _sortOption
    ) { doctor, range, date, status, sort ->
        DoctorAppointmentUiState(
            doctor = doctor,
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
                if (state.doctor == null) {
                    emit(emptyList())
                    return@flow
                }

                val list = when(state.selectedRange) {
                    TimeRange.Month -> {
                        val ym = YearMonth.from(state.currentDate)
                        appointmentRepository.getDoctorAppointmentsByMonth(
                            doctorId,
                            ym.atDay(1).format(isoFormatter)
                        )
                    }
                    TimeRange.All -> appointmentRepository.getAllDoctorAppointments(doctorId)
                    TimeRange.Day -> appointmentRepository.getDoctorAppointmentsByDate(
                        doctorId,
                        state.currentDate.format(isoFormatter)
                    )
                    TimeRange.Week -> {
                        val start = state.currentDate.with(weekFields.dayOfWeek(), 1)
                        (0 until 7).flatMap { offset ->
                            appointmentRepository.getDoctorAppointmentsByDate(doctorId,start.plusDays(offset.toLong()).format(isoFormatter))
                        }
                    }
                }
                emit(list)
            }

            // apply filter and sort
            rawFlow.map { list ->
                list.filter { state.filterStatus.isEmpty() || it.status in state.filterStatus }
                    .let { filtered ->
                        when(state.sortOption) {
                            DoctorSortOption.TimeAsc -> filtered.sortedBy { it.appointmentDate }
                            DoctorSortOption.TimeDesc -> filtered.sortedByDescending { it.appointmentDate}
                            DoctorSortOption.PatientName -> filtered.sortedBy { it.patientName }
                            DoctorSortOption.Status -> filtered.sortedBy { it.status.value }
                        }
                    }
            }
                .map { sortedList ->
                    DoctorAppointmentUiState(
                        doctor = state.doctor,
                        appointments = sortedList,
                        isLoading = false,
                        selectedRange = state.selectedRange,
                        currentDate = state.currentDate,
                        filterStatus = state.filterStatus,
                        sortOption = state.sortOption
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, DoctorAppointmentUiState())

    // UI event handlers
    /** Sets the time range for filtering appointments. */
    fun setRange(range: TimeRange) { _selectedRange.value = range }
    /** Sets the date for filtering appointments. */
    fun setDate(date: LocalDate) { _currentDate.value = date }
    /** Sets the status filter for appointments. */
    fun setFilter(status: Set<AppointmentStatus?>) { _filterStatus.value = status }
    /** Sets the sorting option for appointments. */
    fun setSort(option: DoctorSortOption) { _sortOption.value = option }
/**
 * Resets all filters and sorting options to their default values.*/
    fun resetAll() {
        _selectedRange.value = TimeRange.All
        _filterStatus.value = emptySet()
        _sortOption.value = DoctorSortOption.TimeDesc
        _currentDate.value = LocalDate.now()
    }

}