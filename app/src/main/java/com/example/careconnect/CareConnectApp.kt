package com.example.careconnect

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.admin.AdminApp
import com.example.careconnect.screens.doctor.DoctorApp
import com.example.careconnect.screens.login.LoginScreen
import com.example.careconnect.screens.patient.PatientApp
import com.example.careconnect.screens.patient.profileinfo.ProfileInfoScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.screens.signup.SignUpScreen
import com.example.careconnect.screens.splash.SplashScreen
import com.example.careconnect.ui.navigation.Route.ADMIN_APP
import com.example.careconnect.ui.navigation.Route.DOCTOR_APP
import com.example.careconnect.ui.navigation.Route.LOGIN_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_APP
import com.example.careconnect.ui.navigation.Route.PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.SIGNUP_ROUTE
import com.example.careconnect.ui.navigation.Route.SPLASH_ROUTE
import com.example.careconnect.ui.theme.CareConnectTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

/**
 * Represents the different types of notifications that can be handled by the application.
 * This sealed class ensures that notification types are well-defined and handled consistently.
 */
sealed class NotificationType {
    /**
     * Represents a notification related to an appointment.
     * @property appointmentId The unique identifier for the appointment.
     * @property userType The type of user this notification is relevant for (e.g., "patient", "doctor").
     */
    data class Appointment(
        val appointmentId: String,
        val userType: String
    ) : NotificationType()

    /**
     * Represents a notification related to a chat message.
     * @property chatId The unique identifier for the chat.
     * @property senderId The unique identifier of the message sender.
     * @property recipientId The unique identifier of the message recipient.
     */
    data class Chat(
        val chatId: String,
        val senderId: String,
        val recipientId: String
    ) : NotificationType()
}

/**
 * Data class to encapsulate notification data, primarily its [type].
 * This is used to pass structured notification information within the app,
 * especially when handling incoming notification intents.
 *
 * @property type The specific [NotificationType] of the notification.
 */
data class NotificationData(
    val type: NotificationType
)

/**
 * The main composable function for the CareConnect application.
 *
 * This function sets up the overall structure of the app, including:
 * - Requesting notification permissions on Android Tiramisu (API 33) and above.
 * - Handling incoming intents to extract notification data if the app was launched from a notification.
 * - Setting up the [CareConnectTheme], [Surface], and [Scaffold] for the UI.
 * - Integrating a [SnackbarHost] for displaying messages.
 * - Initializing and providing the [CareConnectNavHost] for navigation.
 *
 * @param modifier Optional [Modifier] for this composable.
 * @param navController The [NavHostController] to manage navigation within the app. Defaults to a new controller.
 * @param getMessage A lambda function that converts a [SnackBarMessage] (which can be a direct string
 *                   or a string resource ID) into a displayable [String].
 * @param intent The [Intent] that launched this composable, used to check for notification data. Defaults to null.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CareConnectApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    getMessage: (SnackBarMessage) -> String,
    intent: Intent? = null
) {
    RequestNotificationPermission() // Requests notification permission if needed.

    val context = LocalContext.current // Provides access to Android context.
    val scope = rememberCoroutineScope() // Coroutine scope tied to this composable's lifecycle.
    val snackbarHostState = remember { SnackbarHostState() } // State for managing snackbar visibility and content.

    // State to hold data extracted from a notification intent.
    var notificationData by remember { mutableStateOf<NotificationData?>(null) }


    // Effect to handle incoming intent data, specifically for notifications.
    // This runs when the `intent` parameter changes.
    LaunchedEffect(intent) {
        intent?.let { currentIntent ->
            val fromNotification = currentIntent.getBooleanExtra("from_notification", false)
            if (fromNotification) {
                // Extract notification details from intent extras.
                val notificationTypeExtra = currentIntent.getStringExtra("notification_type") ?: ""
                val chatIdExtra = currentIntent.getStringExtra("chat_id") ?: ""
                val senderIdExtra = currentIntent.getStringExtra("sender_id") ?: ""
                val recipientIdExtra = currentIntent.getStringExtra("recipient_id") ?: ""
                val appointmentIdExtra = currentIntent.getStringExtra("appointment_id") ?: ""
                val userTypeExtra = currentIntent.getStringExtra("user_type") ?: ""

                // Construct NotificationData based on the extracted type.
                notificationData = when (notificationTypeExtra) {
                    "CHAT_MESSAGE" -> NotificationData(
                        type = NotificationType.Chat(
                            chatId = chatIdExtra,
                            senderId = senderIdExtra,
                            recipientId = recipientIdExtra
                        )
                    )
                    // Defaults to Appointment type if not CHAT_MESSAGE or if type is missing/unknown.
                    else -> NotificationData(
                        type = NotificationType.Appointment(
                            appointmentId = appointmentIdExtra,
                            userType = userTypeExtra
                        )
                    )
                }
            }
        }
    }

    // Lambda to simplify showing snackbar messages.
    val showSnackbar: (SnackBarMessage) -> Unit = { snackBarMsg ->
        val resolvedMessage = getMessage(snackBarMsg) // Resolve SnackBarMessage to a String.
        scope.launch { snackbarHostState.showSnackbar(resolvedMessage) }
    }

    CareConnectTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                // The main navigation host for the application.
                CareConnectNavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    startDestination = SPLASH_ROUTE, // Initial screen of the app.
                    snackbarHostState = snackbarHostState, // Pass state for potential snackbar use in screens.
                    showSnackBar = showSnackbar, // Function to allow screens to show snackbars.
                    notificationData = notificationData // Pass notification data to the NavHost.
                )
            }
        }
    }
}

/**
 * A composable function that requests the `POST_NOTIFICATIONS` permission
 * on Android Tiramisu (API 33) and above.
 *
 * It uses the Accompanist Permissions library to handle the permission request flow.
 * The permission request is launched automatically if it's not already granted.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    // Notification permission is only needed for Android 13 (Tiramisu) and higher.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // State for managing the POST_NOTIFICATIONS permission.
        val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

        // Effect to launch the permission request if not granted.
        // Runs once when the composable enters the composition.
        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

/**
 * Composable function that defines the navigation graph for the CareConnect application.
 *
 * It uses a [NavHost] to manage navigation between different screens (composables)
 * based on defined routes.
 *
 * @param modifier Optional [Modifier] for this composable.
 * @param navController The [NavHostController] used for navigation.
 * @param startDestination The route of the initial screen to be displayed. Defaults to [SPLASH_ROUTE].
 * @param showSnackBar A lambda function that screens can call to display a [SnackBarMessage].
 * @param snackbarHostState The [SnackbarHostState] to be used by the [SnackbarHost],
 *                          potentially allowing screens to directly interact with it if needed.
 * @param notificationData Optional [NotificationData] that might have been received from an
 *                         incoming notification. This can be passed to relevant screens.
 */
@Composable
fun CareConnectNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = SPLASH_ROUTE,
    showSnackBar: (SnackBarMessage) -> Unit,
    snackbarHostState: SnackbarHostState, // Parameter included but not directly used in this NavHost example
    // It's passed down from CareConnectApp, could be used by screens
    // directly or through a ViewModel.
    notificationData: NotificationData? = null
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        // Defines the splash screen.
        composable(SPLASH_ROUTE) {
            SplashScreen(
                openLoginScreen = {
                    navController.navigate(LOGIN_ROUTE) { launchSingleTop = true }
                },
                openPatientScreen = {
                    navController.navigate(PATIENT_APP) { launchSingleTop = true }
                },
                openDoctorScreen = {
                    navController.navigate(DOCTOR_APP) { launchSingleTop = true }
                },
                openAdminScreen = {
                    navController.navigate(ADMIN_APP) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar,
                notificationData = notificationData // Pass notification data to splash screen
            )
        }

        // Defines the login screen.
        composable(LOGIN_ROUTE) {
            LoginScreen(
                openSignUpScreen = {
                    navController.navigate(SIGNUP_ROUTE) { launchSingleTop = true }
                },
                openSplashScreen = { // Navigation back to splash, typically for logout or session expiry.
                    navController.navigate(SPLASH_ROUTE) {
                        launchSingleTop = true
                    }
                },
                openProfileScreen = { // After successful login if profile is incomplete.
                    navController.navigate(PROFILE_ROUTE) {
                        launchSingleTop = true
                    }
                },
                showSnackBar = showSnackBar
            )
        }

        // Defines the sign-up screen.
        composable(SIGNUP_ROUTE) {
            SignUpScreen(
                openProfileScreen = { // After successful sign-up to complete profile.
                    navController.navigate(PROFILE_ROUTE) {
                        popUpTo(SIGNUP_ROUTE) { inclusive = true } // Clear sign-up from back stack
                        launchSingleTop = true
                    }
                },
                openLoginScreen = { // Navigate back to login, e.g., if user decides not to sign up.
                    navController.navigate(LOGIN_ROUTE) {
                        popUpTo(SIGNUP_ROUTE) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                showSnackBar = showSnackBar
            )
        }

        // Defines the profile information screen.
        composable(PROFILE_ROUTE) {
            ProfileInfoScreen(
                openSplashScreen = { // After profile completion, navigate to main app or splash.
                    navController.navigate(SPLASH_ROUTE) {
                        popUpTo(PROFILE_ROUTE) { inclusive = true } // Clear profile from back stack
                        launchSingleTop = true
                    }
                },
                showSnackBar = showSnackBar
            )
        }

        // Defines the settings screen.
        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = { // Typically for logout.
                    navController.navigate(SPLASH_ROUTE) {
                        launchSingleTop = true
                    }
                },
                showSnackBar = showSnackBar
            )
        }

        // Defines the patient-specific part of the application.
        composable(PATIENT_APP){
            PatientApp(
                openSplashScreen = { // Typically for logout.
                    navController.navigate(SPLASH_ROUTE) {
                        launchSingleTop = true
                    }
                },
                notificationData = notificationData, // Pass notification data to patient app
                showSnackBar = showSnackBar
            )
        }

        // Defines the admin-specific part of the application.
        composable(ADMIN_APP) {
            AdminApp(
                openSplashScreen = { // Typically for logout.
                    navController.navigate(SPLASH_ROUTE) {
                        launchSingleTop = true
                    }
                },
                showSnackBar = showSnackBar
            )
        }

        // Defines the doctor-specific part of the application.
        composable(DOCTOR_APP) {
            DoctorApp(
                openSplashScreen = { // Typically for logout.
                    navController.navigate(SPLASH_ROUTE) {
                        launchSingleTop = true
                    }
                },
                notificationData = notificationData, // Pass notification data to doctor app
                showSnackBar = showSnackBar
            )
        }
    }
}