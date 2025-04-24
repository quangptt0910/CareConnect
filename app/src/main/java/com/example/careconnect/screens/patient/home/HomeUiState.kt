package com.example.careconnect.screens.patient.home

import com.example.careconnect.dataclass.Doctor

data class HomeUiState(
    val selectedDoctors: List<Doctor> = emptyList(),
    val suggestions: List<Doctor> = emptyList(),
    val isLoading: Boolean = false,
    val SnackBarMessage: String? = null,
    val searchQuery: String = ""
)