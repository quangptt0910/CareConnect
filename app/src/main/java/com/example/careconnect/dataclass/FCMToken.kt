package com.example.careconnect.dataclass

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Represents an FCM token used for push notifications.
 *
 * @property userId Unique identifier of the user (Firestore document ID).
 * @property fcmToken The Firebase Cloud Messaging token for the device.
 * @property deviceId A unique identifier for the user's device.
 * @property platform The platform on which the app is running (default: "android").
 * @property updatedAt The timestamp when the token was last updated (automatically set by Firestore).
 */
data class UserFCMToken(
    @DocumentId val userId: String = "",
    val fcmToken: String = "",
    val deviceId: String = "",
    val platform: String = "android",
    @ServerTimestamp val updatedAt: Date? = null
)
