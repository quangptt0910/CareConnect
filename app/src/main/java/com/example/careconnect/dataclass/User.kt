package com.example.careconnect.dataclass

/*
* Data class for user account
* id: Firebase UID
* email: user email
* role: user role (admin, doctor, patient)
* userData: data for user by role
*/
data class User(
    val id: String = "",  // FirebaseAuth UID
    val email: String = "",
    val role: Role,
    val userData: UserData
)

sealed class UserData {
    data class AdminData(val name: Administrator) : UserData()
    data class DoctorData(val profile: Doctor) : UserData()
    data class PatientData(val profile: Patient) : UserData()

}

enum class Role {
    ADMIN, DOCTOR, PATIENT
}

