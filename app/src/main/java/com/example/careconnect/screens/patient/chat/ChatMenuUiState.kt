package com.example.careconnect.screens.patient.chat

/**
 * Represents the UI state for the chat menu screen.
 *
 * @property isLoading Whether the screen is currently loading data.
 * @property SnackBarMessage Optional message to show in a snackbar.
 * @property searchQuery The current text input for searching chat rooms.
 */
data class ChatMenuUiState (
    val isLoading: Boolean = false,
    val SnackBarMessage: String? = null,
    val searchQuery: String = ""
)