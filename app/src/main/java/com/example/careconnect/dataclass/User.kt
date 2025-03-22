package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

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
    val schedule: DoctorSchedule = DoctorSchedule()
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

    val height: Double = 0.0,
    val weight: Double = 0.0,
    val medicalHistory: MedicalHistory = MedicalHistory(),  // Patient's medical history or conditions
)


enum class Role {
    ADMIN, DOCTOR, PATIENT
}

enum class Gender {
    MALE, FEMALE, OTHER
}



