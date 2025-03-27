package com.example.careconnect

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.ErrorMessage

import com.example.careconnect.screens.admin.home.HomeScreenAdmin
import com.example.careconnect.screens.login.LoginScreen
import com.example.careconnect.screens.patient.home.HomeScreenPatient
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.screens.signup.SignUpScreen
import com.example.careconnect.screens.splash.SplashScreen
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.navigation.Route.HomeDoctorRoute
import com.example.careconnect.ui.navigation.Route.HomePatientRoute
import com.example.careconnect.ui.navigation.Route.LoginRoute
import com.example.careconnect.ui.navigation.Route.SettingsRoute
import com.example.careconnect.ui.navigation.Route.SignUpRoute
import com.example.careconnect.ui.navigation.Route.SplashRoute
import com.example.careconnect.ui.navigation.Route.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


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

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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
                            startDestination = SplashRoute,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<SplashRoute> {
                                SplashScreen(
                                    openLoginScreen = {
                                        navController.navigate(LoginRoute) { launchSingleTop = true }
                                    },
                                    openPatientScreen = {
                                        navController.navigate(HomePatientRoute) { launchSingleTop = true }
                                    },
                                    openDoctorScreen = {
                                        navController.navigate(HomeDoctorRoute) { launchSingleTop = true }
                                    },
                                    openAdminScreen = {
                                        navController.navigate(HomeAdminRoute) { launchSingleTop = true }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )
                            }
                            composable<LoginRoute> {
                                LoginScreen(
                                    openSignUpScreen = {
                                        navController.navigate(SignUpRoute) { launchSingleTop = true }
                                    },
                                    openSplashScreen = {
                                        navController.navigate(SplashRoute) { launchSingleTop = true }
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
                                        navController.navigate(SettingsRoute) { launchSingleTop = true }
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

                            composable<SettingsRoute> {
                                SettingsScreen(
                                    openSplashScreen = {
                                        navController.navigate(SplashRoute) { launchSingleTop = true }
                                    },
                                    showErrorSnackbar = { errorMessage ->
                                        val message = getErrorMessage(errorMessage)
                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                    }
                                )
                            }

                            composable<HomeAdminRoute> {
                                HomeScreenAdmin(
                                    openSettingsScreen = {
                                        navController.navigate(SettingsRoute) { launchSingleTop = true }
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


