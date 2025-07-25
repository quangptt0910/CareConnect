package com.example.careconnect.notifications


import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.example.careconnect.dataclass.UserFCMToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Manages Firebase Cloud Messaging (FCM) tokens for the current authenticated user.
 * This includes fetching, storing, and updating FCM tokens in Firestore.
 *
 * @property context Application context used for device ID storage.
 * @property auth Firebase Authentication instance to get the current user.
 * @property firestore Firebase Firestore instance used to store FCM token data.
 */
@Singleton
class FCMTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    /**
     * Fetches the current FCM token and updates Firestore with it, along with a device ID and platform info.
     */
    suspend fun updateFCMToken() {
        try {
            val currentUser = auth.currentUser ?: return
            val token = FirebaseMessaging.getInstance().token.await()

            val deviceId = getDeviceId(context)

            val userToken = UserFCMToken(
                fcmToken = token,
                deviceId = deviceId,
                platform = "android",
            )

            // Store in separate collection for easy access
            firestore.collection("user_tokens")
                .document(currentUser.uid)
                .set(userToken)
                .await()

            Log.d("FCMTokenManager", "Token updated successfully")
        } catch (e: Exception) {
            Log.e("FCMTokenManager", "Failed to update FCM token", e)
        }
    }

    /**
     * Retrieves or generates a unique device ID and persists it locally.
     *
     * @param context The application context used to access SharedPreferences.
     * @return The unique device ID.
     */
    private fun getDeviceId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var deviceId = sharedPreferences.getString("device_id", null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            sharedPreferences.edit { putString("device_id", deviceId) }
        }

        return deviceId
    }

    /**
     * Debug utility to log and verify the current FCM token and Firestore data.
     * Also re-invokes [updateFCMToken] after printing the current state.
     */
    suspend fun debugFCMToken() {
        try {
            val currentUser = auth.currentUser
            Log.d("FCMTokenManager", "Current user: ${currentUser?.uid}")

            if (currentUser == null) {
                Log.e("FCMTokenManager", "No authenticated user")
                return
            }

            val token = FirebaseMessaging.getInstance().token.await()
            Log.d("FCMTokenManager", "FCM Token: $token")

            // Check if token exists in Firestore
            val tokenDoc = firestore.collection("user_tokens")
                .document(currentUser.uid)
                .get()
                .await()

            if (tokenDoc.exists()) {
                Log.d("FCMTokenManager", "Existing token in Firestore: ${tokenDoc.data}")
                println("Debug: Existing token in Firestore: ${tokenDoc.data}")
            } else {
                Log.w("FCMTokenManager", "No token found in Firestore for user: ${currentUser.uid}")
            }

            // Update token
            updateFCMToken()

        } catch (e: Exception) {
            Log.e("FCMTokenManager", "Error in debugFCMToken", e)
        }
    }
}



