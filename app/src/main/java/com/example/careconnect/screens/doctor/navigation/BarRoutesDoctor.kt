package com.example.careconnect.screens.doctor.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.R
import com.example.careconnect.ui.navigation.Route.DOCTOR_APPOINTMENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_CHAT_MENU_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.HOME_DOCTOR_ROUTE


/**
 * Enum class representing the routes for the bottom navigation bar in the doctor's section of the app.
 * Each route has a title, an icon, and a navigation route string.
 *
 * @property title The string resource ID for the title of the navigation item.
 * @property icon The [ImageVector] for the icon of the navigation item.
 * @property route The navigation route string associated with the item.
 */
enum class BarRoutesDoctor(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.feed, Icons.Filled.Home, HOME_DOCTOR_ROUTE),
    CHAT(R.string.chat, Icons.AutoMirrored.Filled.Chat, DOCTOR_CHAT_MENU_ROUTE),
    PATIENTS(R.string.patients, Icons.Filled.People, DOCTOR_PATIENTS_ROUTE),
    APPOINTMENTS(R.string.appointments, Icons.Filled.CalendarToday, DOCTOR_APPOINTMENTS_ROUTE),
    PROFILE(R.string.profile, Icons.Filled.Person, DOCTOR_PROFILE_ROUTE)

}