package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/*
* Data class for user account
* id: Firebase UID
* email: user email
* role: user role (admin, doctor, patient) mostly help with the login
*/
data class Admin(
    @DocumentId val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: Role = Role.ADMIN,
    val phone: String = "",
)

data class Doctor(
    @DocumentId val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: Role = Role.DOCTOR,
    val phone: String = "",
    val address: String = "",
    val specialization: String = "",
    val experience: Int = 2025, // Year start to work
    //val schedule: DoctorSchedule = DoctorSchedule(),
    var profilePhoto: String = "",
    @ServerTimestamp val createdAt: Date? = null
)

data class Patient(
    @DocumentId val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: Role = Role.PATIENT,
    val phone: String = "",
    val address: String = "",
    val dateOfBirth: String = "",
    val gender: Gender = Gender.MALE,
    val pesel: String = "",
    val height: Double = 0.0,
    val weight: Double = 0.0,

    // Enhanced fields for better auth tracking (optional)
    val authProviders: List<String> = emptyList(), // ["password", "google.com"]
    val hasEmailAuth: Boolean = false,
    val hasGoogleAuth: Boolean = false,
    val profileComplete: Boolean = false, // Track if profile setup is complete
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    // Helper methods
    fun hasMultipleAuthMethods(): Boolean = authProviders.size > 1
    fun getFullName(): String = "$name $surname".trim()
}

/**
 * Get user's authentication provider information
 */
enum class AuthProvider {
    NOT_SIGNED_IN,
    EMAIL_ONLY,
    GOOGLE_ONLY,
    BOTH_LINKED,
    UNKNOWN
}

enum class Role {
    ADMIN, DOCTOR, PATIENT
}

enum class Gender {
    MALE, FEMALE, OTHER
}

// Data class to hold the reference to a patient
data class PatientRef(
    @DocumentId val id: String = "",
    @ServerTimestamp val addedAt: Date? = null
)

