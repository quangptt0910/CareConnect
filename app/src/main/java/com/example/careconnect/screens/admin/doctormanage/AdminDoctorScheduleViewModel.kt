package com.example.careconnect.screens.admin.doctormanage

import androidx.lifecycle.SavedStateHandle
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.DoctorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Represents the UI state for the doctor's schedule screen.
 *
 * @property selectedDates Set of dates currently selected as working days for the doctor.
 * @property isLoading True if the working days data is currently being loaded.
 * @property isSaving True if the selected working days are currently being saved.
 * @property snackBarMessage Optional message to show in a snackbar, usually for errors.
 * @property navigateNext Flag indicating whether to navigate to the next screen.
 */
data class DoctorScheduleUiState(
    val selectedDates: Set<LocalDate> = emptySet(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val snackBarMessage: String? = null,
    val navigateNext: Boolean = false
)

/**
 * ViewModel responsible for managing the doctor's schedule UI state and interactions.
 *
 * @property doctorRepository Repository handling doctor data operations.
 * @property savedStateHandle SavedStateHandle to access navigation arguments.
 */
@HiltViewModel
class AdminDoctorScheduleViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository,
    savedStateHandle: SavedStateHandle
): MainViewModel() {
    private val _uiState = MutableStateFlow(DoctorScheduleUiState())
    val uiState: StateFlow<DoctorScheduleUiState> = _uiState.asStateFlow()

    private val doctorId: String = checkNotNull(savedStateHandle["doctorId"])

    init {
        loadWorkingDays()
    }

    /**
     * Saves the selected working days for the doctor.
     *
     * @param doctorId The doctor's unique identifier.
     * @param selectedDate Set of dates selected as working days.
     */
    suspend fun saveWorkingDays(doctorId: String, selectedDate: Set<LocalDate>) {
        doctorRepository.saveWorkingDays(doctorId, selectedDate)
    }

    /**
     * Loads the current working days for the doctor from the repository.
     *
     * Updates the UI state accordingly, including handling errors.
     */
    private fun loadWorkingDays() {
        // Implement the logic to load working days
        launchCatching {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                doctorRepository.getWorkingDays(doctorId).collect { workingDays ->
                    _uiState.value = _uiState.value.copy(selectedDates = workingDays)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(snackBarMessage = e.message)
            }
        }
    }

    /**
     * Toggles the selection of a given date.
     *
     * If the date is currently selected, it will be deselected; otherwise, it will be added.
     *
     * @param date The date to toggle.
     */
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

    /**
     * Saves the selected dates and triggers navigation to the next screen.
     *
     * Handles UI loading states and error messages.
     */
    fun onSaveAndNext() {
        launchCatching {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                doctorRepository.saveWorkingDays(doctorId, _uiState.value.selectedDates)
                _uiState.value = _uiState.value.copy(isSaving = false, navigateNext = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, snackBarMessage = e.message)
            }
        }
    }
}