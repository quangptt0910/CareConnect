package com.example.careconnect.dataclass

data class Doctor(
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val specialization: String = "",
    val experience: Int = 2025 // Year start to work
)
