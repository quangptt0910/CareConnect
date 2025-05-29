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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.notifications.FCMTokenManager
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

//    LaunchedEffect(Unit) {
//        scope.launch {
//            try {
//                val tokenManager = FCMTokenManager(
//                    context = context,
//                    auth = FirebaseAuth.getInstance(),
//                    firestore = FirebaseFirestore.getInstance()
//                )
//                tokenManager.updateFCMToken()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val tokenManager = FCMTokenManager(context, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
                tokenManager.updateFCMToken()

                // Add verification
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val tokenDoc = FirebaseFirestore.getInstance()
                        .collection("user_tokens")
                        .document(currentUser.uid)
                        .get()
                        .await()    

                    Log.d("FCMDebug", "Token exists in DB: ${tokenDoc.exists()}")
                    if (tokenDoc.exists()) {
                        Log.d("FCMDebug", "Token data: ${tokenDoc.data}")
                    }
                }
            } catch (e: Exception) {
                Log.e("FCMDebug", "Token update failed", e)
            }
        }
    }

    LaunchedEffect(intent) {
        intent?.let {
            val notificationType = it.getStringExtra("notification_type")
            val appointmentId = it.getStringExtra("appointment_id")
            if (notificationType != null && appointmentId != null) {
                // Navigate based on notification type
                navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
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
                    showSnackBar = showSnackbar
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
    snackbarHostState: SnackbarHostState
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
                showSnackBar = showSnackBar
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


