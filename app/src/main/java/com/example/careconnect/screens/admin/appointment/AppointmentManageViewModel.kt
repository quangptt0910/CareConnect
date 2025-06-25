package com.example.careconnect.screens.admin.appointment

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AppointmentRepository
import com.example.careconnect.dataclass.Appointment
import com.example.careconnect.dataclass.AppointmentStatus
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
 * Represents the possible time ranges for filtering appointments.
 */
enum class TimeRange { Day, Week, Month, All }

/**
 * Sorting options available for appointment list display.
 *
 * @property label Human-readable label describing the sort order.
 */
enum class SortOption(val label: String) {
    TimeAsc("Time: Earliest"),
    TimeDesc("Time: Latest"),
    PatientName("Patient Name"),
    DoctorName("Doctor Name"),
    Status("Status")
}

/**
 * Represents the UI state for [AppointmentManageScreen], including
 * the list of appointments, loading/error states, and filter/sort selections.
 *
 * @property appointments The list of filtered and sorted appointments to display.
 * @property isLoading Whether data is currently being loaded.
 * @property error Any error message encountered during loading, or null if none.
 * @property selectedRange The currently selected time range filter.
 * @property currentDate The currently selected date (used for filtering).
 * @property filterStatus The set of appointment statuses currently used to filter appointments.
 * @property sortOption The current sorting option for the appointments.
 */
data class AppointmentUiState(
    val appointments: List<Appointment> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,

    val selectedRange: TimeRange = TimeRange.Day,
    val currentDate: LocalDate = LocalDate.now(),

    val filterStatus: Set<AppointmentStatus?> = emptySet(),
    val sortOption: SortOption = SortOption.TimeAsc
)

/**
 * ViewModel for managing appointment data and UI state in the admin appointment screen.
 *
 * Fetches appointment data from [AppointmentRepository] based on current filter, date range,
 * and sort selections. Exposes [uiState] as a [StateFlow] to be collected by the UI.
 *
 * @property repo The appointment repository used to fetch appointment data.
 */
@HiltViewModel
class AppointmentManageViewModel @Inject constructor(
    private val repo: AppointmentRepository
) : MainViewModel() {

    private val isoFormatter = DateTimeFormatter.ISO_DATE
    private val weekFields = WeekFields.of(Locale.getDefault())

    // Controls
    private val _selectedRange = MutableStateFlow(TimeRange.Day)
    private val _currentDate   = MutableStateFlow(LocalDate.now())
    private val _filterStatus  = MutableStateFlow<Set<AppointmentStatus?>>(emptySet())
    private val _sortOption    = MutableStateFlow(SortOption.TimeAsc)

    // Combined UI state
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AppointmentUiState> = combine(
        _selectedRange,
        _currentDate,
        _filterStatus,
        _sortOption
    ) { range, date, status, sort ->
        AppointmentUiState(
            selectedRange = range,
            currentDate   = date,
            filterStatus  = status,
            sortOption    = sort,
            isLoading     = true
        )
    }
        .flatMapLatest { state ->
            // fetch raw list based on range
            val rawFlow: Flow<List<Appointment>> = flow {
                val list = when(state.selectedRange) {
                    TimeRange.Day -> repo.getAllAppointmentsByDate(state.currentDate.format(isoFormatter))
                    TimeRange.Week -> {
                        val start = state.currentDate.with(weekFields.dayOfWeek(), 1)
                        (0 until 7).flatMap { offset ->
                            repo.getAllAppointmentsByDate(start.plusDays(offset.toLong()).format(isoFormatter))
                        }
                    }
                    TimeRange.Month -> {
                        val ym = YearMonth.from(state.currentDate)
                        repo.getAllAppointmentsByMonth(ym.atDay(1).format(isoFormatter))
                    }
                    TimeRange.All -> {
                        repo.getAllAppointments()
                    }
                }
                emit(list)
            }
            // apply filter and sort
            rawFlow.map { list ->
                list.filter { state.filterStatus.isEmpty() || it.status in state.filterStatus }
                    .let { filtered ->
                        when(state.sortOption) {
                            SortOption.TimeAsc    -> filtered.sortedBy { it.startTime }
                            SortOption.TimeDesc   -> filtered.sortedByDescending { it.startTime }
                            SortOption.PatientName-> filtered.sortedBy { it.patientName }
                            SortOption.DoctorName -> filtered.sortedBy { it.doctorName }
                            SortOption.Status     -> filtered.sortedBy { it.status.value }
                        }
                    }
            }
                .map { sortedList ->
                    AppointmentUiState(
                        appointments = sortedList,
                        isLoading    = false,
                        selectedRange = state.selectedRange,
                        currentDate   = state.currentDate,
                        filterStatus  = state.filterStatus,
                        sortOption    = state.sortOption
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, AppointmentUiState())

    // UI event handlers
    /**
     * Updates the selected time range filter.
     * @param range The new [TimeRange] to select.
     */
    fun setRange(range: TimeRange)  { _selectedRange.value = range }

    /**
     * Updates the selected date used for filtering appointments.
     * @param date The new [LocalDate] to select.
     */
    fun setDate(date: LocalDate)   { _currentDate.value   = date }

    /**
     * Updates the set of appointment statuses used as filters.
     * @param status The new set of [AppointmentStatus] filters.
     */
    fun setFilter(status: Set<AppointmentStatus?>) { _filterStatus.value = status }

    /**
     * Updates the current sorting option for appointments.
     * @param option The new [SortOption] to apply.
     */
    fun setSort(option: SortOption)  { _sortOption.value  = option }

    /**
     * Resets all filters, sorting options, and date selections to their default states.
     */
    fun resetAll() {
        _selectedRange.value = TimeRange.All
        _currentDate.value   = LocalDate.now()
        _filterStatus.value = emptySet()
        _sortOption.value   = SortOption.TimeAsc
    }
}