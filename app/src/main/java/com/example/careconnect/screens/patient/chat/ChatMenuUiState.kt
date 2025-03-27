package com.example.careconnect.screens.patient.chat

import com.example.careconnect.dataclass.Doctor

data class ChatMenuUiState (
    val selectedDoctors: List<Doctor> = emptyList(),
    val suggestions: List<Doctor> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = ""
)