package com.example.careconnect.dataclass

data class Doctor(
    val phone: String = "",
    val address: String = "",
    val specialization: String = "",
    val experience: Int = 2025, // Year start to work
    val schedule: DoctorSchedule = DoctorSchedule()
)
