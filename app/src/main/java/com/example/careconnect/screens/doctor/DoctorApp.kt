package com.example.careconnect.screens.doctor

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.dataclass.SnackBarMessage
import com.example.careconnect.screens.doctor.home.HomeScreenDoctor
import com.example.careconnect.screens.doctor.navigation.BarRoutesDoctor
import com.example.careconnect.screens.doctor.navigation.BottomBarDoctor
import com.example.careconnect.screens.patient.chat.ChatMenuScreen
import com.example.careconnect.screens.settings.SettingsScreen
import com.example.careconnect.ui.navigation.Route.HOME_DOCTOR_ROUTE
import com.example.careconnect.ui.navigation.Route.SETTINGS_ROUTE
import com.example.careconnect.ui.navigation.Route.getPatientChatRoute

@Composable
fun DoctorApp(
    openSplashScreen: () -> Unit = {},
    showSnackBar: (SnackBarMessage) -> Unit = {}
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        bottomBar = {
            BottomBarDoctor(
                tabs = BarRoutesDoctor.entries.toTypedArray(),
                navController = navController,
                navigateToRoute = { route -> navController.navigate(route) }
            )
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = BarRoutesDoctor.FEED.route,
            modifier = Modifier.padding(it)
        ) {
            composable(BarRoutesDoctor.FEED.route) {
                HomeScreenDoctor(
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) }
                )
            }
            composable(BarRoutesDoctor.CHAT.route) {
                ChatMenuScreen(
                    openChatScreen = { chatId, doctorId ->
                        navController.navigate(getPatientChatRoute(doctorId, chatId))
                    }
                )
            }
            composable(BarRoutesDoctor.PROFILE.route) {

            }

            composable(SETTINGS_ROUTE) {
                SettingsScreen(
                    openSplashScreen = openSplashScreen,
                    showSnackBar = showSnackBar
                )
            }

            composable(HOME_DOCTOR_ROUTE) {
                HomeScreenDoctor(
                    openSettingsScreen = { navController.navigate(SETTINGS_ROUTE) }
                )
            }
        }
    }
}