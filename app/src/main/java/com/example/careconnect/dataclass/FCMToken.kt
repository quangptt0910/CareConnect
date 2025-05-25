package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserFCMToken(
    @DocumentId val userId: String = "",
    val fcmToken: String = "",
    val deviceId: String = "",
    val platform: String = "android",
    @ServerTimestamp val updatedAt: Date? = null
)
