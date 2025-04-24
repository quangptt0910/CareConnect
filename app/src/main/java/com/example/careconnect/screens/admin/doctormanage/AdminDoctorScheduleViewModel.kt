package com.example.careconnect.screens.admin.doctormanage

import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

/**
 * UI State for Doctor Schedule
 */
data class DoctorScheduleUiState(
    val selectedDates: Set<LocalDate> = emptySet(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val navigateNext: Boolean = false
)

@HiltViewModel
class AdminDoctorScheduleViewModel @Inject constructor(
    private val doctorId: String,
    private val doctorRepository: DoctorRepository
): MainViewModel() {
    private val _uiState = MutableStateFlow(DoctorScheduleUiState())
    val uiState: StateFlow<DoctorScheduleUiState> = _uiState.asStateFlow()

    init {
        loadWorkingDays()
    }

    suspend fun saveWorkingDays(doctorId: String, selectedDate: Set<LocalDate>) {
        doctorRepository.saveWorkingDays(doctorId, selectedDate)
    }

    private fun loadWorkingDays(

    ) {
        // Implement the logic to load working days
        launchCatching {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                doctorRepository.getWorkingDays(doctorId).collect { workingDays ->
                    _uiState.value = _uiState.value.copy(selectedDates = workingDays)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = e.message)
            }
        }
    }

    fun toggleDate(date: LocalDate) {
        // Implement the logic to toggle the selected date
        val currentDates = _uiState.value.selectedDates
        val updatedDates = if (date in currentDates) {
            currentDates - date
        } else {
            currentDates + date
        }
        _uiState.value = _uiState.value.copy(selectedDates = updatedDates)
        }

    fun onSaveAndNext() {
        launchCatching {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                doctorRepository.saveWorkingDays(doctorId, _uiState.value.selectedDates)
                _uiState.value = _uiState.value.copy(isSaving = false, navigateNext = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = e.message)
            }
        }
    }

}