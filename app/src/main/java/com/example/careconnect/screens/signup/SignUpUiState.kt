package com.example.careconnect.screens.signup

/**
 * Data class representing the UI state for the sign-up screen.
 *
 * @property name The first name entered by the user.
 * @property surname The last name entered by the user.
 * @property email The email address entered by the user.
 * @property password The password entered by the user.
 */
data class SignUpUiState(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val password: String = "",
)