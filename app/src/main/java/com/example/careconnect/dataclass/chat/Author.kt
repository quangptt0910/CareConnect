package com.example.careconnect.dataclass.chat

import com.example.careconnect.dataclass.Role

data class Author(
    val id: String = "",  // Unique ID for each author
    val name: String = "", // Display name (optional)
    val role: Role = Role.PATIENT, // Role of the author (optional)
)