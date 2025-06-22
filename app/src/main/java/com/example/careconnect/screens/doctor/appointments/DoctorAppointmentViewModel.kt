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

enum class TimeRange { Day, Week, Month, All }
enum class DoctorSortOption(val label: String) {
    TimeAsc("Time: Earliest"),
    TimeDesc("Time: Latest"),
    PatientName("Patient Name"),
    Status("Status")
    //  TODO() implement to search for patients name
}


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
    fun setRange(range: TimeRange) { _selectedRange.value = range }
    fun setDate(date: LocalDate) { _currentDate.value = date }
    fun setFilter(status: Set<AppointmentStatus?>) { _filterStatus.value = status }
    fun setSort(option: DoctorSortOption) { _sortOption.value = option }
    fun resetAll() {
        _selectedRange.value = TimeRange.All
        _filterStatus.value = emptySet()
        _sortOption.value = DoctorSortOption.TimeDesc
        _currentDate.value = LocalDate.now()
    }

}