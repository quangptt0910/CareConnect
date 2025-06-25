package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.time.LocalDate
import java.util.Date


/**
 * Represents a doctor's schedule for a specific date.
 *
 * @property id Unique identifier for the schedule (Firestore document ID).
 * @property date Date for which this schedule is applicable (format: YYYY-MM-DD).
 * @property timeSlots List of time slots available on this day.
 * @property isWorkingDay Indicates whether the doctor is working on this date.
 * @property createdAt Timestamp when the schedule was created.
 * @property updatedAt Timestamp when the schedule was last updated.
 */
data class DoctorSchedule(
    @DocumentId val id: String = "",
    val date: String = "",
    val timeSlots: List<TimeSlot> = emptyList(),
    val isWorkingDay: Boolean = true,
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

/**
 * Enum representing the type of time slot.
 */
enum class SlotType { CONSULT, SURGERY }

/**
 * Represents a single time slot within a doctor's schedule.
 *
 * @property startTime Start time of the slot (format: HH:mm).
 * @property endTime End time of the slot (format: HH:mm).
 * @property appointmentMinutes Duration of the appointment in minutes.
 * @property slotType Type of the slot (consultation or surgery).
 * @property available Whether the time slot is currently available.
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
/**
 * Converts a [LocalDate] to ISO date string (YYYY-MM-DD).
 */
fun LocalDate.toDateString(): String = this.toString() // Returns in ISO format (YYYY-MM-DD)

/**
 * Parses a string in ISO format (YYYY-MM-DD) to [LocalDate].
 */
fun String.toLocalDate(): LocalDate = LocalDate.parse(this) // Parses from ISO format (YYYY-MM-DD)
