package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId

data class Task(
    @DocumentId val id: String = "",
    var name: String = "",
    var isChecked: Boolean = false
)
