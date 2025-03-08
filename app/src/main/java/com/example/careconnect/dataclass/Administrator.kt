package com.example.careconnect.dataclass

data class Administrator(
    val id: String = "",  // FirebaseAuth UID
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val role: String = "",  // "admin", "doctor", "patient"
    val createdAt: com.google.firebase.Timestamp? = null
)
