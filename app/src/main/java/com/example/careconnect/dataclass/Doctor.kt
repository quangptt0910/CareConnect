package com.example.careconnect.dataclass

data class Doctor(
    val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val speciality: String = "", //type of doctor
    val role: String = "doctor",  // "admin", "doctor", "patient"
    val createdAt: com.google.firebase.Timestamp? = null
)
