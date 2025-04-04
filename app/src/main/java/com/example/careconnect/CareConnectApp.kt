package com.example.careconnect

//import androidx.navigation.navigation
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.screens.admin.AdminApp
import com.example.careconnect.screens.login.LoginScreen
import com.example.careconnect.screens.patient.PatientApp
import com.example.careconnect.screens.patient.home.HomeScreenPatient
import com.example.careconnect.screens.patient.profileinfo.ProfileInfoScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.screens.signup.SignUpScreen
import com.example.careconnect.screens.splash.SplashScreen
import com.example.careconnect.ui.navigation.Route.ADMIN_APP
import com.example.careconnect.ui.navigation.Route.HOME_DOCTOR_ROUTE
import com.example.careconnect.ui.navigation.Route.HOME_PATIENT_ROUTE
import com.example.careconnect.ui.navigation.Route.LOGIN_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_APP
import com.example.careconnect.ui.navigation.Route.PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.SIGNUP_ROUTE
import com.example.careconnect.ui.navigation.Route.SPLASH_ROUTE
import kotlinx.coroutines.launch


@Composable
fun CareConnectNavHost(
    navController: NavHostController,
    startDestination: String = SPLASH_ROUTE,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
                showErrorSnackbar = { errorMessage ->
                    val message = getErrorMessage(errorMessage)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
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
                showErrorSnackbar = { errorMessage ->
                    val message = getErrorMessage(errorMessage)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            )
        }

        composable(HOME_PATIENT_ROUTE) {
            HomeScreenPatient(
                openSettingsScreen = {
                    navController.navigate(SETTINGS_ROUTE) { launchSingleTop = true }
                }
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
                showErrorSnackbar = { errorMessage ->
                    val message = getErrorMessage(errorMessage)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            )
        }
        composable(PROFILE_ROUTE) {
            ProfileInfoScreen(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = { errorMessage ->
                    val message = getErrorMessage(errorMessage)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            )
        }

        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                },
                showErrorSnackbar = { errorMessage ->
                    val message = getErrorMessage(errorMessage)
                    scope.launch { snackbarHostState.showSnackbar(message) }
                }
            )
        }

        composable(PATIENT_APP){
            PatientApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                }
            )
        }

        composable(ADMIN_APP) {
            AdminApp(
                openSplashScreen = {
                    navController.navigate(SPLASH_ROUTE) { launchSingleTop = true }
                }
            )
        }
    }
}

fun getErrorMessage(error: ErrorMessage): String {
    return when (error) {
        is ErrorMessage.StringError -> error.message
        is ErrorMessage.IdError -> error.message.toString()
    }
}