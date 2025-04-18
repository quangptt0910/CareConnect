package com.example.careconnect

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.screens.admin.AdminApp
import com.example.careconnect.screens.login.LoginScreen
import com.example.careconnect.screens.patient.PatientApp
import com.example.careconnect.screens.patient.profileinfo.ProfileInfoScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.screens.signup.SignUpScreen
import com.example.careconnect.screens.splash.SplashScreen
import com.example.careconnect.ui.navigation.Route.ADMIN_APP
import com.example.careconnect.ui.navigation.Route.HOME_DOCTOR_ROUTE
import com.example.careconnect.ui.navigation.Route.LOGIN_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_APP
import com.example.careconnect.ui.navigation.Route.PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.SIGNUP_ROUTE
import com.example.careconnect.ui.navigation.Route.SPLASH_ROUTE
import com.example.careconnect.ui.theme.CareConnectTheme
import kotlinx.coroutines.launch


@Composable
fun CareConnectApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = SPLASH_ROUTE,
    getErrorMessage: (ErrorMessage) -> String,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val showErrorSnackbar: (ErrorMessage) -> Unit = { errorMessage ->
        val message = getErrorMessage(errorMessage)
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
                    showErrorSnackBar = showErrorSnackbar
                )
            }
        }
    }
}

@Composable
fun CareConnectNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = SPLASH_ROUTE,
    showErrorSnackBar: (ErrorMessage) -> Unit,
    snackbarHostState: SnackbarHostState,
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
                    navController.navigate(HOME_DOCTOR_ROUTE) { launchSingleTop = true }
                },
                openAdminScreen = {
                    navController.navigate(ADMIN_APP) { launchSingleTop = true }
                },
                showErrorSnackbar = showErrorSnackBar
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
                showErrorSnackbar = showErrorSnackBar
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
                showErrorSnackbar = showErrorSnackBar
            )
        }

        composable(PROFILE_ROUTE) {
            ProfileInfoScreen(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = showErrorSnackBar
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = showErrorSnackBar
            )
        }

        composable(PATIENT_APP){
            PatientApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = showErrorSnackBar
            )
        }

        composable(ADMIN_APP) {
            AdminApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = showErrorSnackBar
            )
        }
    }
}


