package com.example.careconnect.dataclass.chat

import com.example.careconnect.dataclass.Role

/**
 * Represents the author of a chat message.
 *
 * @property id Unique identifier of the author (e.g., Firebase UID).
 * @property name Display name of the author.
 * @property role The role of the author (e.g., PATIENT, DOCTOR).
 */
data class Author(
    val id: String = "",  // Unique ID for each author
    val name: String = "", // Display name (optional)
    val role: Role = Role.PATIENT, // Role of the author (optional)
)