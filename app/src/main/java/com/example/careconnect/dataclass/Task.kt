package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

/**
 * Represents a task item, such as a checklist entry.
 *
 * @property id Firestore document ID of the task.
 * @property name Name or description of the task.
 * @property isChecked Whether the task has been completed.
 */
data class Task(
    @DocumentId val id: String = "",
    var name: String = "",
    var isChecked: Boolean = false
)
