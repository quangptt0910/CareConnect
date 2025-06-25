package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents an admin account in the system.
 *
 * @property id Firebase UID of the admin.
 * @property name Admin's first name.
 * @property surname Admin's last name.
 * @property email Admin's email address.
 * @property role User role, default is ADMIN.
 * @property phone Admin's phone number.
 */
data class Admin(
    @DocumentId val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: Role = Role.ADMIN,
    val phone: String = "",
)

/**
 * Represents a doctor's profile in the system.
 *
 * @property id Firebase UID of the doctor.
 * @property name Doctor's first name.
 * @property surname Doctor's last name.
 * @property email Doctor's email address.
 * @property role User role, default is DOCTOR.
 * @property phone Doctor's phone number.
 * @property address Practice or clinic address.
 * @property specialization Doctor's area of specialization.
 * @property experience Year the doctor started practicing.
 * @property profilePhoto URL to the doctor's profile image.
 * @property createdAt Timestamp when the profile was created.
 */
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

/**
 * Represents a patient profile in the system.
 *
 * @property id Firebase UID of the patient.
 * @property name Patient's first name.
 * @property surname Patient's last name.
 * @property email Patient's email address.
 * @property role User role, default is PATIENT.
 * @property phone Patient's contact number.
 * @property address Residential address.
 * @property dateOfBirth Date of birth as string.
 * @property gender Gender of the patient.
 * @property pesel National ID or health number.
 * @property height Patient's height in cm.
 * @property weight Patient's weight in kg.
 * @property authProviders List of authentication methods used.
 * @property hasEmailAuth Indicates if email/password auth is linked.
 * @property hasGoogleAuth Indicates if Google auth is linked.
 * @property profileComplete Indicates if user completed profile setup.
 * @property createdAt Account creation time in epoch milliseconds.
 * @property lastLoginAt Last login time in epoch milliseconds.
 */
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
    /**
     * Checks if the patient uses more than one sign-in method.
     */
    fun hasMultipleAuthMethods(): Boolean = authProviders.size > 1

    /**
     * Returns the full name of the patient.
     */
    fun getFullName(): String = "$name $surname".trim()
}

/**
 * Describes the authentication provider used by the user.
 */
enum class AuthProvider {
    NOT_SIGNED_IN,
    EMAIL_ONLY,
    GOOGLE_ONLY,
    BOTH_LINKED,
    UNKNOWN
}

/**
 * Enum class for defining user roles within the system.
 */
enum class Role {
    ADMIN, DOCTOR, PATIENT
}

/**
 * Enum class representing gender options.
 */
enum class Gender {
    MALE, FEMALE, OTHER
}

// Data class to hold the reference to a patient
/**
 * Represents a lightweight reference to a patient, typically used in nested documents.
 *
 * @property id Firebase UID of the referenced patient.
 * @property addedAt Timestamp of when the reference was created.
 */
data class PatientRef(
    @DocumentId val id: String = "",
    @ServerTimestamp val addedAt: Date? = null
)

