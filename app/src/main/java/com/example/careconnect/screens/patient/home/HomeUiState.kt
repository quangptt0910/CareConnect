package com.example.careconnect.screens.patient.home

import com.example.careconnect.dataclass.Doctor

/**
 * Represents the UI state for the home screen's doctor search functionality.
 *
 * @property selectedDoctors List of doctors currently selected by the user.
 * @property suggestions List of suggested doctors matching the search query.
 * @property isLoading Indicates if the search results are currently loading.
 * @property SnackBarMessage Optional error or informational message to display.
 * @property searchQuery The current search query string entered by the user.
 */
data class HomeUiState(
    val selectedDoctors: List<Doctor> = emptyList(),
    val suggestions: List<Doctor> = emptyList(),
    val isLoading: Boolean = false,
    val SnackBarMessage: String? = null,
    val searchQuery: String = ""
)