package com.example.careconnect.dataclass

data class Patient(
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val dateOfBirth: String = "",
    val gender: Gender,

    val height: Double = 0.0,
    val weight: Double = 0.0,
    val medicalHistory: MedicalHistory = MedicalHistory(),  // Patient's medical history or conditions
//    val allergies: List<String> = emptyList(),  // Allergies (can be a list or just a string)
)

enum class Gender {
    MALE,
    FEMALE,
    OTHER
}
