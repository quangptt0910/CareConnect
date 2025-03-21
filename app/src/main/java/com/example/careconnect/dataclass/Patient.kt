package com.example.careconnect.dataclass

data class Patient(
    val phone: String = "",
    val address: String = "",
    val dateOfBirth: String = "",
    val gender: Gender = Gender.MALE,

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
