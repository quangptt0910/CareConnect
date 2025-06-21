package com.example.careconnect.screens.patient.chat

data class ChatMenuUiState (
    val isLoading: Boolean = false,
    val SnackBarMessage: String? = null,
    val searchQuery: String = ""
)