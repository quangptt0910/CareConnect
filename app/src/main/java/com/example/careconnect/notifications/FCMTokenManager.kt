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

@Singleton
class FCMTokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun updateFCMToken() {
        try {
            val currentUser = auth.currentUser ?: return
            val token = FirebaseMessaging.getInstance().token.await()

            val deviceId = getDeviceId(context)

            val userToken = UserFCMToken(
                userId = currentUser.uid,
                fcmToken = token,
                deviceId = deviceId,
                platform = "android"
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

    private fun getDeviceId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        var deviceId = sharedPreferences.getString("device_id", null)

        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            sharedPreferences.edit { putString("device_id", deviceId) }
        }

        return deviceId
    }
}



