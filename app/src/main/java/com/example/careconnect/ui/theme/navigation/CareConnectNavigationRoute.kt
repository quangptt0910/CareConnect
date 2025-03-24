package com.example.careconnect.ui.theme.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable data object SplashRoute : Route()
    @Serializable data object LoginRoute : Route()
    @Serializable data object SignUpRoute : Route()
    @Serializable data object HomePatientRoute : Route()
    @Serializable data object HomeDoctorRoute : Route()
    @Serializable data object HomeAdminRoute : Route()
}

@Stable
class CareConnectNavigation(
    val navController: NavHostController,
) {
    fun popUp() {
        navController.popBackStack()
    }

    fun navigateTo(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) {saveState = true}
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}
