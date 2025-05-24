package com.example.careconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.careconnect.dataclass.SnackBarMessage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        render(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        enableEdgeToEdge()
        render(intent)
    }

    private fun render(intent: Intent) {
        setContent {
            CareConnectApp(
                getMessage = { message ->
                    when (message) {
                        is SnackBarMessage.StringMessage -> message.message
                        is SnackBarMessage.IdMessage     -> getString(message.message)
                    }
                },
                intent = intent
            )
        }
    }

}