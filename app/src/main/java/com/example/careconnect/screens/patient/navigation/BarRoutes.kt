package com.example.careconnect.screens.patient.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.R
import com.example.careconnect.ui.navigation.Route.HOME_PATIENT_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_APPOINTMENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_CHAT_MENU_ROUTE
import com.example.careconnect.ui.navigation.Route.PATIENT_PROFILE_ROUTE

/**
 * Enum representing the bottom navigation bar routes for the Patient section of the app.
 *
 * Each route has an associated title resource ID, an icon, and a navigation route string.
 *
 * @property title The string resource ID for the title shown in the UI.
 * @property icon The icon displayed for this route in the navigation bar.
 * @property route The navigation route string associated with this screen.
 */
enum class BarRoutes(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.feed, Icons.Filled.Home, HOME_PATIENT_ROUTE),
    CHAT(R.string.chat, Icons.AutoMirrored.Filled.Chat, PATIENT_CHAT_MENU_ROUTE),
    APPOINTMENTS(R.string.appointments, Icons.Filled.CalendarMonth, PATIENT_APPOINTMENTS_ROUTE),
    PROFILE(R.string.profile, Icons.Filled.Person, PATIENT_PROFILE_ROUTE)

}