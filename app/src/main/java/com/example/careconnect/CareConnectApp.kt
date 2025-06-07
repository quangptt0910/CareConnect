package com.example.careconnect

import android.Manifest
import android.content.Intent
import android.os.Build
import android.util.Log
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

data class NotificationData(
    val type: String,
    val appointmentId: String,
    val userType: String
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CareConnectApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    getMessage: (SnackBarMessage) -> String,
    intent: Intent? = null
) {
    RequestNotificationPermission()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var notificationHandled by remember { mutableStateOf(false) }
    var notificationData by remember { mutableStateOf<NotificationData?>(null) }

//    LaunchedEffect(Unit) {
//        scope.launch {
//            try {
//                val tokenManager = FCMTokenManager(context, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
//                tokenManager.updateFCMToken()
//
//                // Add verification
//                val currentUser = FirebaseAuth.getInstance().currentUser
//                if (currentUser != null) {
//                    val tokenDoc = FirebaseFirestore.getInstance()
//                        .collection("user_tokens")
//                        .document(currentUser.uid)
//                        .get()
//                        .await()
//
//                    Log.d("FCMDebug", "Token exists in DB: ${tokenDoc.exists()}")
//                    if (tokenDoc.exists()) {
//                        Log.d("FCMDebug", "Token data: ${tokenDoc.data}")
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("FCMDebug", "Token update failed", e)
//            }
//        }
//    }

    // Handle notification navigation
    LaunchedEffect(intent) {
        intent?.let { notificationIntent ->
            val fromNotification = notificationIntent.getBooleanExtra("from_notification", false)
            val notificationType = notificationIntent.getStringExtra("notification_type")
            val appointmentId = notificationIntent.getStringExtra("appointment_id")
            val userType = notificationIntent.getStringExtra("user_type")

            Log.d("NotificationDebug", "From notification: $fromNotification")
            Log.d("NotificationDebug", "Notification type: $notificationType")
            Log.d("NotificationDebug", "User type: $userType")
            Log.d("NotificationDebug", "Appointment ID: $appointmentId")

            if (fromNotification && !notificationHandled) {
                notificationHandled = true

                // Store notification data for splash screen to handle
                notificationData = NotificationData(
                    type = notificationType ?: "",
                    appointmentId = appointmentId ?: "",
                    userType = userType ?: ""
                )

                Log.d("NotificationDebug", "Notification data stored for splash screen")
            }
        }
    }

    val showSnackbar: (SnackBarMessage) -> Unit = { message ->
        val message = getMessage(message)
        scope.launch { snackbarHostState.showSnackbar(message) }
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
                CareConnectNavHost(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    startDestination = SPLASH_ROUTE,
                    snackbarHostState = snackbarHostState,
                    showSnackBar = showSnackbar,
                    notificationData = notificationData
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}

@Composable
fun CareConnectNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = SPLASH_ROUTE,
    showSnackBar: (SnackBarMessage) -> Unit,
    snackbarHostState: SnackbarHostState,
    notificationData: NotificationData? = null
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
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
                notificationData = notificationData
            )
        }

        composable(LOGIN_ROUTE) {
            LoginScreen(
                openSignUpScreen = {
                    navController.navigate(SIGNUP_ROUTE) { launchSingleTop = true }
                },
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                openProfileScreen = {
                    navController.navigate(PROFILE_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }

        composable(SIGNUP_ROUTE) {
            SignUpScreen(
                openProfileScreen = {
                    navController.navigate(PROFILE_ROUTE) { launchSingleTop = true }
                },
                openLoginScreen = {
                    navController.navigate(LOGIN_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }

        composable(PROFILE_ROUTE) {
            ProfileInfoScreen(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }

        composable(PATIENT_APP){
            PatientApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }

        composable(ADMIN_APP) {
            AdminApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }

        composable(DOCTOR_APP) {
            DoctorApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showSnackBar = showSnackBar
            )
        }
    }
}


