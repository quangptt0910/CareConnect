package com.example.careconnect.screens.signup


import android.util.Patterns
import java.util.regex.Pattern

private const val MIN_PASSWORD_LENGTH = 8
private const val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

/**
 * Extension function to validate if the string is a valid email address.
 *
 * Uses Android's built-in [Patterns.EMAIL_ADDRESS] to verify the email format.
 *
 * @return `true` if the string is a valid email, `false` otherwise.
 */
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Extension function to validate if the string is a strong password.
 *
 * Checks for minimum length ([MIN_PASSWORD_LENGTH]) and pattern defined in [PASSWORD_PATTERN].
 *
 * @return `true` if the string meets the password strength requirements, `false` otherwise.
 */
fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= MIN_PASSWORD_LENGTH &&
            Pattern.compile(PASSWORD_PATTERN).matcher(this).matches()
}