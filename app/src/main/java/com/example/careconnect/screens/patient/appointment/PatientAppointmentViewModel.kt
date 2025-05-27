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

enum class PatientTimeRange { Month, All }
enum class PatientSortOption(val label: String) {
    TimeAsc("Time: Earliest"),
    TimeDesc("Time: Latest"),
    DoctorName("Doctor Name"),
    Status("Status")
}


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

    private fun loadPatient() {
        launchCatching {
            val patient = patientRepository.getPatientById(patientId)
            _patient.value = patient
        }
    }

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
                            PatientSortOption.TimeAsc -> filtered.sortedBy { it.startTime }
                            PatientSortOption.TimeDesc -> filtered.sortedByDescending { it.startTime }
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
    fun setRange(range: PatientTimeRange) { _selectedRange.value = range }
    fun setDate(date: LocalDate) { _currentDate.value = date }
    fun setFilter(status: Set<AppointmentStatus?>) { _filterStatus.value = status }
    fun setSort(option: PatientSortOption) { _sortOption.value = option }
    fun resetAll() {
        _selectedRange.value = PatientTimeRange.All
        _filterStatus.value = emptySet()
        _sortOption.value = PatientSortOption.TimeAsc
        _currentDate.value = LocalDate.now()
    }

}