package com.example.careconnect.model

import androidx.compose.ui.semantics.Role

/*
* Interface for manage user account by role
* Administrator: manage all users, doctor can only be added by admin, ...
* Doctor: manage own profile and patient list, ...
* Patient: manage own profile, appointment list, ...
*/
interface AccountService {
    // User authentication
    suspend fun login(email: String, password: String): Result<String>
    suspend fun signUp(email: String, password: String): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>

    // Admin - User management
    suspend fun getAllUsers(role: Role ?= null)
    suspend fun getUserById(userId: String)
    suspend fun updateUser(userId: String, updates: Map<String, Any>)
    suspend fun deleteUser(userId: String)

    // Doctor profile management
    suspend fun updateDoctorProfile(doctorId: String, updates: Map<String, Any>)
    suspend fun getDoctorAvailability(doctorId: String, date: String)
    suspend fun updateDoctorAvailability(doctorId: String)

    // Patient profile management
    suspend fun updatePatientProfile(patientId: String, updates: Map<String, Any>)
    suspend fun getPatientProfile(patientId: String)

    // Appointment management
//    suspend fun createAppointment(appointment: Appointment)
//    suspend fun updateAppointment(appointmentId: String, updates: Map<String, Any>)
//    suspend fun deleteAppointment(appointmentId: String)

    // Current User
    suspend fun getCurrentUser()
    suspend fun updateCurrentUser(updates: Map<String, Any>)

    // Doctor listing and filtering (for patients)
    suspend fun getAvailableDoctors(specialization: String? = null, date: String? = null)



}