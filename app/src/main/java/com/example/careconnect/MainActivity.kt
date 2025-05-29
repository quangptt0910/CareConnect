package com.example.careconnect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.careconnect.dataclass.SnackBarMessage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        render(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        enableEdgeToEdge()
        setIntent(intent)
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


    private fun handleNotificationIntent(intent: Intent) {
        if (intent.getBooleanExtra("from_notification", false)) {
            val notificationType = intent.getStringExtra("notification_type")
            val appointmentId = intent.getStringExtra("appointment_id")
            val userType = intent.getStringExtra("user_type")
            val notificationAction = intent.getStringExtra("notification_action")
        }
    }

}