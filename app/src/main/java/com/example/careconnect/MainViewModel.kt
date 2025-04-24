package com.example.careconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careconnect.dataclass.SnackBarMessage
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class MainViewModel : ViewModel() {
    fun launchCatching(
        showSnackbar: (SnackBarMessage) -> Unit = {},
        block: suspend CoroutineScope.() -> Unit
    ) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Firebase.crashlytics.recordException(throwable)
                val error = if (throwable.message.isNullOrBlank()) {
                    SnackBarMessage.IdMessage(R.string.generic_error)
                } else {
                    SnackBarMessage.StringMessage(throwable.message!!)
                }
                showSnackbar(error)
            },
            block = block
        )
}