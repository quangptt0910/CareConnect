package com.example.careconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.ErrorMessage
import com.example.careconnect.screens.admin.home.HomeAdminRoute
import com.example.careconnect.screens.doctor.HomeDoctorRoute
import com.example.careconnect.screens.login.LoginRoute
import com.example.careconnect.screens.login.LoginScreen
import com.example.careconnect.screens.patient.home.HomePatientRoute
import com.example.careconnect.screens.patient.home.HomeScreenPatient
import com.example.careconnect.screens.signup.SignUpRoute
import com.example.careconnect.screens.signup.SignUpScreen
import com.example.careconnect.ui.theme.CareConnectTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()
            val snackbarHostState = remember { SnackbarHostState() }
            val navController = rememberNavController()

            CareConnectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = HomePatientRoute,
                            modifier = Modifier.padding(innerPadding)
                        ) {

                            composable<LoginRoute> {
                                LoginScreen(
                                    openHomeScreenPatient = {
                                        navController.navigate(HomePatientRoute) { launchSingleTop = true }
                                    },
                                    openHomeScreenDoctor = {
                                        navController.navigate(HomeDoctorRoute) { launchSingleTop = true }
                                    },
                                    openHomeScreenAdmin = {
                                        navController.navigate(HomeAdminRoute) { launchSingleTop = true }
                                    },
                                    openSignUpScreen = {
                                        navController.navigate(SignUpRoute) { launchSingleTop = true }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )
                            }

                            composable<HomePatientRoute> {
                                HomeScreenPatient(
                                    openSettingsScreen = {
                                        navController.navigate(LoginRoute) { launchSingleTop = true }
                                    }
                                )

                            }

                            composable<SignUpRoute> {
                                SignUpScreen(
                                    openHomeScreen = {
                                        navController.navigate(HomePatientRoute) { launchSingleTop = true }
                                    },
                                    openLoginScreen = {
                                        navController.navigate(LoginRoute) { launchSingleTop = true }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )
                            }

                        }
                    }
                }
            }
        }

    }

    private fun getErrorMessage(error: ErrorMessage): String {
        return when (error) {
            is ErrorMessage.StringError -> error.message
            is ErrorMessage.IdError -> this@MainActivity.getString(error.message)
        }
    }
}


