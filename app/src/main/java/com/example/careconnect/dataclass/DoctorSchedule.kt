package com.example.careconnect.dataclass

import java.time.LocalDate


/**
 * Represents a doctor's schedule for available days and time slots.
 * @property workingDays A map of day names to a list of available time slots.
 *  - Key: date string (yyyy-mm-dd), value: list of available time slots for that day.
 * @property defaultWorkingHours A list of default time slots for the doctor's schedule.
 */
data class DoctorSchedule(
    val workingDays: Map<String, List<TimeSlot>> = emptyMap(),  // Map of day names to a list of available time slots>
    val defaultWorkingHours: List<TimeSlot> = listOf(
        TimeSlot("09:00", "12:00"),
        TimeSlot("14:00", "18:00")
    )
) {

}

/**
 * Represents a time slot for a doctor's schedule.
 */
data class TimeSlot(
    val startTime: String = "",  // Start time HH:MM (e.g., "09:00")
    val endTime: String = "",    // End time HH:MM (e.g., "09:15")
    val isAvailable: Boolean = true  // Whether the time slot is available for booking
) {

}



// Extension functions to help with date conversions
fun LocalDate.toDateString(): String = this.toString() // Returns in ISO format (YYYY-MM-DD)
fun String.toLocalDate(): LocalDate = LocalDate.parse(this) // Parses from ISO format (YYYY-MM-DD)

/** Turn a TimeSlot into a JSON-friendly Map */
fun TimeSlot.toMap(): Map<String, Any> = mapOf(
    "startTime"   to startTime,
    "endTime"     to endTime,
    "isAvailable" to isAvailable
)

/** Turn a DoctorSchedule into a JSON-friendly Map */
fun DoctorSchedule.toMap(): Map<String, Any> = mapOf(
    "workingDays"         to workingDays.mapValues { (_, slots) ->
        slots.map { it.toMap() }
    },
    "defaultWorkingHours" to defaultWorkingHours.map { it.toMap() }
)
