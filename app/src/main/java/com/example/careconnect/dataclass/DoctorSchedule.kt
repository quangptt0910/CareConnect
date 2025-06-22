package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date


/**
 * Represents a doctor's schedule for available days and time slots.
 */
data class DoctorSchedule(
    @DocumentId val id: String = "",
    val date: String = "",
    val timeSlots: List<TimeSlot> = emptyList(),
    val isWorkingDay: Boolean = true,
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

enum class SlotType { CONSULT, SURGERY }

/**
 * Represents a time slot for a doctor's schedule.
 */
data class TimeSlot(
    val startTime: String = "",  // Start time HH:MM (e.g., "09:00")
    val endTime: String = "",    // End time HH:MM (e.g., "09:15")
    val appointmentMinutes: Int = 15,
    val slotType: SlotType = SlotType.CONSULT,
    val available: Boolean = true  // Whether the time slot is available for booking
) {
    constructor() : this("", "", 15, SlotType.CONSULT, true)
}

// Extension functions to help with date conversions
fun LocalDate.toDateString(): String = this.toString() // Returns in ISO format (YYYY-MM-DD)
fun String.toLocalDate(): LocalDate = LocalDate.parse(this) // Parses from ISO format (YYYY-MM-DD)
