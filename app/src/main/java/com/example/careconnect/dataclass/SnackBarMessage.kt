package com.example.careconnect.dataclass

import androidx.annotation.StringRes

sealed class SnackBarMessage {
    class StringMessage(val message: String) : SnackBarMessage()
    class IdMessage(@StringRes val message: Int) : SnackBarMessage()
}