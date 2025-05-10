package com.example.careconnect.screens.doctor.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.careconnect.R
import com.example.careconnect.ui.navigation.Route.DOCTOR_APPOINTMENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_CHAT_MENU_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PATIENTS_ROUTE
import com.example.careconnect.ui.navigation.Route.DOCTOR_PROFILE_ROUTE
import com.example.careconnect.ui.navigation.Route.HOME_DOCTOR_ROUTE

enum class BarRoutesDoctor(
    @StringRes val title: Int,
    val icon: ImageVector,
    val route: String
) {
    FEED(R.string.feed, Icons.Filled.Home, HOME_DOCTOR_ROUTE),
    CHAT(R.string.chat, Icons.AutoMirrored.Filled.Chat, DOCTOR_CHAT_MENU_ROUTE),
    PATIENTS(R.string.patients, Icons.Filled.People, DOCTOR_PATIENTS_ROUTE),
    APPOINTMENTS(R.string.appointments, Icons.Filled.CalendarToday, DOCTOR_APPOINTMENTS_ROUTE),
    PROFILE(R.string.profile, Icons.Filled.CalendarToday, DOCTOR_PROFILE_ROUTE)

}