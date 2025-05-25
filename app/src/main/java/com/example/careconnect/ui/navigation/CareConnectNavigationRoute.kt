package com.example.careconnect.ui.navigation

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController

object Route {
    // Shared Routes
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val SIGNUP_ROUTE = "signup"
    const val PROFILE_ROUTE = "profile"
    const val SETTINGS_ROUTE = "settings"

    // Role-based Home Routes
    const val HOME_PATIENT_ROUTE = "patient/home/{specialty}"
    const val HOME_DOCTOR_ROUTE = "doctor/home"
    const val HOME_ADMIN_ROUTE = "admin/home"


    // Admin-specific Routes
    const val ADMIN_APP = "admin_app"
    const val ADMIN_GRAPH = "admin_graph"
    const val ADMIN_DOCTOR_MANAGE_ROUTE = "admin/doctors"
    const val ADMIN_DOCTOR_ADD_ROUTE = "admin/doctors/add"
    const val ADMIN_DOCTOR_EDIT_ROUTE = "admin/doctors/edit/{doctorId}"
    const val ADMIN_PATIENT_MANAGE_ROUTE = "admin/patients"
    const val ADMIN_APPOINTMENTS_ROUTE = "admin/appointments"
    const val ADMIN_PROFILE_ROUTE = "admin/profile"
    const val ADMIN_DOCTOR_SCHEDULE_ROUTE = "admin/doctors/schedule/{doctorId}"
    const val ADMIN_DOCTOR_SCHEDULE_EDIT_ROUTE = "admin/doctors/schedule/edit"

    // Patient-specific Routes
    const val PATIENT_APP = "patient_app"
    const val PATIENT_GRAPH = "patient_graph"
    const val PATIENT_PROFILE_ROUTE = "patient/profile"
    const val PATIENT_APPOINTMENTS_ROUTE = "patient/appointments"
    const val PATIENT_CHAT_MENU_ROUTE = "patient/chat/menu"
    const val PATIENT_CHAT_ROUTE = "patient/chat/{doctorId}/{patientId}/{chatId}"
    const val PATIENT_DOCTORS_OVERVIEW = "patient/doctors/overview/{specialty}"
    const val PATIENT_DOCTORS_PROFILE = "patient/doctors/profile/{doctorId}"
    const val PATIENT_BOOKING_APPOINTMENTS_ROUTE = "patient/appointments/book/{doctorId}"

    // Doctor-specific Routes
    const val DOCTOR_APP = "doctor_app"
    const val DOCTOR_PROFILE_ROUTE = "doctor/profile"
    const val DOCTOR_CHAT_ROUTE = "doctor/chat/{chatId}/{patientId}/{doctorId}"
    const val DOCTOR_CHAT_MENU_ROUTE = "doctor/chat/menu"
    const val DOCTOR_PATIENTS_ROUTE = "doctor/patients"
    const val DOCTOR_PATIENTS_PROFILE_ROUTE = "doctor/patients/profile/{patientId}"
    const val DOCTOR_PATIENTS_MEDICAL_HISTORY_SECTION_ROUTE = "doctor/patients/medical_history/{patientId}/{section}"
    const val DOCTOR_PATIENTS_MEDICAL_REPORT_ROUTE = "doctor/patients/medical_report/{patientId}"
    const val DOCTOR_PATIENTS_CREATE_MEDICAL_REPORT_ROUTE = "doctor/patients/create_medical_report/{patientId}"
    const val DOCTOR_PATIENTS_PRESCRIPTIONS_ROUTE = "doctor/patients/prescriptions/{patientId}"
    const val DOCTOR_PATIENTS_CREATE_PRESCRIPTIONS_ROUTE = "doctor/patients/create_prescriptions/{patientId}"
    const val DOCTOR_APPOINTMENTS_ROUTE = "doctor/appointments"
    const val DOCTOR_SCHEDULE_ROUTE = "doctor/schedule"

    // Route with parameter helper
    fun adminDoctorEditRoute(doctorId: String) =
        ADMIN_DOCTOR_EDIT_ROUTE.replace("{doctorId}", doctorId)

    fun adminDoctorScheduleRoute(doctorId: String) =
        ADMIN_DOCTOR_SCHEDULE_ROUTE.replace("{doctorId}", doctorId)

    fun getPatientChatRoute(doctorId: String, patientId:String, chatId: String) =
        "patient/chat/$doctorId/$patientId/$chatId"

    fun getPatientDoctorsOverviewRoute(specialty: String) =
        "patient/doctors/overview/$specialty"

    fun getPatientDoctorsProfileRoute(doctorId: String) =
        "patient/doctors/profile/$doctorId"

    fun getDoctorPatientsProfileRoute(patientId: String) =
        "doctor/patients/profile/$patientId"

    fun getDoctorPatientsMedicalReportRoute(patientId: String) =
        "doctor/patients/medical_report/$patientId"

    fun getPatientBookingAppointmentsBookRoute(doctorId: String) =
        "patient/appointments/book/$doctorId"

    fun getDoctorPatientsCreateMedicalReportRoute(patientId: String) =
        "doctor/patients/create_medical_report/$patientId"

    fun getDoctorChatRoute(chatId: String, patientId:String, doctorId: String) =
        "doctor/chat/$chatId/$patientId/$doctorId"

    fun getDoctorPatientsMedicalHistorySectionRoute(patientId: String, section: String) =
        "doctor/patients/medical_history/$patientId/$section"

    fun getDoctorPatientsCreatePrescriptionsRoute(patientId: String) =
        "doctor/patients/create_prescriptions/$patientId"

    fun getDoctorPatientsPrescriptionsRoute(patientId: String) =
        "doctor/patients/prescriptions/$patientId"


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

/*
 * TODO()
 *  make navBuilder for each Roles
 */