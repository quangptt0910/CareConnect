package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

/*
* Data class for user account
* id: Firebase UID
* email: user email
* role: user role (admin, doctor, patient)
* userData: data for user by role
*/
data class User(
    @DocumentId val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val role: Role = Role.PATIENT,
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

