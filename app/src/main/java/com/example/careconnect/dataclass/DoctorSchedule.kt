package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

data class DoctorSchedule(
    @DocumentId val id: String = "",  // Firestore document ID (optional)
    val doctorId: String = "",
    val availability: List<DaySchedule> = listOf(),  // List of available days with time slots
)

data class DaySchedule(
    val date: String = "",  // Date in format "yyyy-MM-dd"
    val availableSlots: List<TimeSlot> = listOf()  // List of time slots for the specific day
)

data class TimeSlot(
    val startTime: String = "",  // Start time (e.g., "09:00 AM")
    val endTime: String = "",    // End time (e.g., "09:15 AM")
    val isAvailable: Boolean = true  // Whether the time slot is available for booking
)

