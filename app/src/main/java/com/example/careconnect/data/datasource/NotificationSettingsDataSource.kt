package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Role
import com.example.careconnect.notifications.AppointmentNotificationSettings
import com.example.careconnect.notifications.ChatNotificationSettings
import com.example.careconnect.notifications.NotificationSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source responsible for managing notification settings in Firestore
 * for both patients and doctors, supporting real-time updates and persistence.
 *
 * @property firestore Firestore database instance.
 * @property auth Firebase authentication instance used to get the current user.
 */
@Singleton
class NotificationSettingsDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        private const val TAG = "NotificationDataSource"
        private const val COLLECTION_PATIENTS = "patients"
        private const val COLLECTION_DOCTORS = "doctors"
        private const val SUBCOLLECTION_NOTIFICATION_SETTINGS = "notification_settings"
        private const val DOCUMENT_SETTINGS = "settings"
    }

    /**
     * Saves the given [NotificationSettings] to Firestore for the specified [role].
     *
     * @param role The role of the current user (PATIENT or DOCTOR).
     * @param settings The settings object to persist.
     * @throws Exception if the user is not authenticated.
     */
    suspend fun saveNotificationSettings(role: Role, settings: NotificationSettings) {
        val currentUser = auth.currentUser ?: throw Exception("User not authenticated")

        val collection = when (role) {
            Role.PATIENT -> COLLECTION_PATIENTS
            Role.DOCTOR -> COLLECTION_DOCTORS
            else -> throw IllegalArgumentException("Unsupported role: $role")
        }

        val settingsMap = mapOf(
            "chatNotifications" to mapOf(
                "enabled" to settings.chatNotifications.enabled,
                "sound" to settings.chatNotifications.sound,
                "vibration" to settings.chatNotifications.vibration,
                "showPreview" to settings.chatNotifications.showPreview
            ),
            "appointmentNotifications" to mapOf(
                "enabled" to settings.appointmentNotifications.enabled,
                "sound" to settings.appointmentNotifications.sound,
                "vibration" to settings.appointmentNotifications.vibration,
                "reminders" to settings.appointmentNotifications.reminders,
                "confirmations" to settings.appointmentNotifications.confirmations,
                "cancellations" to settings.appointmentNotifications.cancellations,
                "completions" to settings.appointmentNotifications.completions
            ),
            "lastUpdated" to System.currentTimeMillis()
        )

        firestore.collection(collection)
            .document(currentUser.uid)
            .collection(SUBCOLLECTION_NOTIFICATION_SETTINGS)
            .document(DOCUMENT_SETTINGS)
            .set(settingsMap)
            .await()

        Log.d(TAG, "Notification settings saved for $role")
    }

    /**
     * Returns a [Flow] of [NotificationSettings] for real-time observation of settings updates.
     *
     * @param role The role of the current user (PATIENT or DOCTOR).
     * @return A [Flow] emitting current or updated notification settings.
     */
    fun getNotificationSettingsFlow(role: Role): Flow<NotificationSettings?> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "User not authenticated")
            trySend(null)
            close()
            return@callbackFlow
        }

        val collection = when (role) {
            Role.PATIENT -> COLLECTION_PATIENTS
            Role.DOCTOR -> COLLECTION_DOCTORS
            else -> {
                Log.e(TAG, "Unsupported role: $role")
                trySend(null)
                close()
                return@callbackFlow
            }
        }

        val listener: ListenerRegistration = firestore
            .collection(collection)
            .document(currentUser.uid)
            .collection(SUBCOLLECTION_NOTIFICATION_SETTINGS)
            .document(DOCUMENT_SETTINGS)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to notification settings", error)
                    trySend(null)
                    return@addSnapshotListener
                }

                try {
                    val settings = if (document?.exists() == true) {
                        parseSettingsFromDocument(document.data ?: emptyMap())
                    } else {
                        Log.d(TAG, "No settings found for $role, using defaults")
                        null
                    }
                    trySend(settings)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing notification settings", e)
                    trySend(null)
                }
            }

        awaitClose {
            listener.remove()
            Log.d(TAG, "Notification settings listener removed for $role")
        }
    }

    /**
     * Fetches the current [NotificationSettings] for the given user role from Firestore.
     *
     * @param role The role of the current user.
     * @return The [NotificationSettings] object or null if not found.
     */
    suspend fun getNotificationSettings(role: Role): NotificationSettings? {
        val currentUser = auth.currentUser ?: return null

        val collection = when (role) {
            Role.PATIENT -> COLLECTION_PATIENTS
            Role.DOCTOR -> COLLECTION_DOCTORS
            else -> return null
        }

        return try {
            val document = firestore
                .collection(collection)
                .document(currentUser.uid)
                .collection(SUBCOLLECTION_NOTIFICATION_SETTINGS)
                .document(DOCUMENT_SETTINGS)
                .get()
                .await()

            if (document.exists()) {
                parseSettingsFromDocument(document.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching notification settings for $role", e)
            null
        }
    }

    /**
     * Parses Firestore document data into a [NotificationSettings] object.
     *
     * @param data The map of fields retrieved from Firestore.
     * @return A [NotificationSettings] instance with values populated.
     */
    private fun parseSettingsFromDocument(data: Map<String, Any>): NotificationSettings {
        val chatMap = data["chatNotifications"] as? Map<String, Any> ?: emptyMap()
        val appointmentMap = data["appointmentNotifications"] as? Map<String, Any> ?: emptyMap()

        val chatSettings = ChatNotificationSettings(
            enabled = chatMap["enabled"] as? Boolean ?: true,
            sound = chatMap["sound"] as? Boolean ?: true,
            vibration = chatMap["vibration"] as? Boolean ?: true,
            showPreview = chatMap["showPreview"] as? Boolean ?: true
        )

        val appointmentSettings = AppointmentNotificationSettings(
            enabled = appointmentMap["enabled"] as? Boolean ?: true,
            sound = appointmentMap["sound"] as? Boolean ?: true,
            vibration = appointmentMap["vibration"] as? Boolean ?: true,
            reminders = appointmentMap["reminders"] as? Boolean ?: true,
            confirmations = appointmentMap["confirmations"] as? Boolean ?: true,
            cancellations = appointmentMap["cancellations"] as? Boolean ?: true,
            completions = appointmentMap["completions"] as? Boolean ?: true
        )

        return NotificationSettings(
            chatNotifications = chatSettings,
            appointmentNotifications = appointmentSettings
        )
    }
}
