package com.example.careconnect.dataclass

import androidx.annotation.StringRes

/**
 * Represents a snackbar message that can either be a raw string or a string resource ID.
 */
sealed class SnackBarMessage {
    /**
     * Message represented as a plain string.
     *
     * @param message The message text to be shown in the snackbar.
     */
    class StringMessage(val message: String) : SnackBarMessage()

    /**
     * Message represented using a string resource ID.
     *
     * @param message Resource ID of the message (e.g., R.string.error_message).
     */
    class IdMessage(@StringRes val message: Int) : SnackBarMessage()
}