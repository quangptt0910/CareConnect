package com.example.careconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.dataclass.SnackBarMessage
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Base ViewModel class providing common functionality for other ViewModels in the application.
 *
 * This class includes a utility function [launchCatching] for launching coroutines
 * with centralized error handling, reporting exceptions to Firebase Crashlytics,
 * and displaying user-friendly error messages via a snackbar.
 */
open class MainViewModel : ViewModel() {
    /**
     * Launches a new coroutine in the [viewModelScope] with a built-in [CoroutineExceptionHandler].
     *
     * This function is designed to simplify coroutine launching within ViewModels by
     * centralizing error handling. Any uncaught exception within the provided [block]
     * will be caught, recorded in Firebase Crashlytics, and a [SnackBarMessage]
     * will be shown to the user.
     *
     * @param showSnackbar A lambda function that takes a [SnackBarMessage] and is responsible
     *                     for displaying it to the user. Defaults to an empty lambda if not provided.
     * @param block The suspend block of code to be executed in the coroutine.
     * @return A [kotlinx.coroutines.Job] representing the launched coroutine.
     */
    fun launchCatching(
        showSnackbar: (SnackBarMessage) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                // Record the exception in Firebase Crashlytics
                Firebase.crashlytics.recordException(throwable)

                // Determine the error message to show
                val error = if (throwable.message.isNullOrBlank()) {
                    SnackBarMessage.IdMessage(R.string.generic_error) // Show a generic error
                } else {
                    SnackBarMessage.StringMessage(throwable.message!!) // Show the specific exception message
                }
                // Display the error message using the provided lambda
                showSnackbar(error)
            },
            block = block
        )
}