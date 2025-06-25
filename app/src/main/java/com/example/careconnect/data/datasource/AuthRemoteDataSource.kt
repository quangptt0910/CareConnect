package com.example.careconnect.data.datasource

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import com.example.careconnect.R
import com.example.careconnect.dataclass.Admin
import com.example.careconnect.dataclass.AuthProvider
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Gender
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val credentialManager: CredentialManager,

) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun getCurrentUserRole(): Role {
        val userId = getCurrentUserId()
        val db = FirebaseFirestore.getInstance()
        val patientDoc = db.collection("patients").document(userId!!).get().await()
        val doctorDoc = db.collection("doctors").document(userId).get().await()
        val adminDoc = db.collection("admins").document(userId).get().await()

        if (patientDoc.exists()) {
            return Role.PATIENT
        } else if (doctorDoc.exists()) {
            return Role.DOCTOR
        } else if (adminDoc.exists()) {
            return Role.ADMIN
        } else {
            throw Exception("User not found")
        }
    }

    val currentUserIdFlow: Flow<String?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { _ -> this.trySend(currentUser?.uid) }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    val currentUserFlow: Flow<UserData>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                launch {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        trySend(UserData.NoUser)
                        return@launch
                    }

                    try {
                        // Try each collection in sequence to find the user
                        val adminDoc = firestore.collection("admins").document(uid).get().await()
                        if (adminDoc.exists()) {
                            val admin = adminDoc.toObject(Admin::class.java)
                            admin?.let { trySend(UserData.AdminData(it)) }
                            return@launch
                        }

                        val doctorDoc = firestore.collection("doctors").document(uid).get().await()
                        if (doctorDoc.exists()) {
                            val doctor = doctorDoc.toObject(Doctor::class.java)
                            doctor?.let { trySend(UserData.DoctorData(it)) }
                            return@launch
                        }

                        val patientDoc = firestore.collection("patients").document(uid).get().await()
                        if (patientDoc.exists()) {
                            val patient = patientDoc.toObject(Patient::class.java)
                            patient?.let { trySend(UserData.PatientData(it)) }
                            return@launch
                        }

                        trySend(UserData.Error)
                    } catch (e: Exception) {
                        Log.e("AuthRemoteDataSource", "Error getting user data", e)
                        println("Debug: Error getting user data")
                        trySend(UserData.Error)
                    }
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }.distinctUntilChanged()

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    /*
    * Link authentication with email and password account (signUp)
    */
    suspend fun signUp(name: String, surname: String, email: String, password: String) {
        // link authentication with email and password account
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        // save patient id to firestore
        val userId = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val patient = Patient(name = name, surname = surname, email = email)
        
        // Create patient document in Firestore as "patients" collection
        db.collection("patients").document(userId).set(patient).await()
    }

    suspend fun linkAccount(userId: String, gender: String, weight: Double, height: Double, dob: String, address: String) {
        val genderEnum = Gender.valueOf(gender)

        firestore.collection("patients")
            .document(userId)
            .update(
                "gender", genderEnum,
                "weight", weight,
                "height", height,
                "dob", dob,
                "address", address
            )
            .await()
    }

    suspend fun signOut() {
        auth.signOut()
        println("AuthRemote: SignedOut clicked")

        // Only clear google credential of signed in with google
        try {
            val currentUser = auth.currentUser
            val googleSignIn = currentUser?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true
            if (googleSignIn || currentGoogleIdToken != null) {
                val clearRequest = ClearCredentialStateRequest(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
                credentialManager.clearCredentialState(clearRequest)
                currentGoogleIdToken = null // Clear the stored token
            }
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            currentGoogleIdToken = null
        }
    }

    suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    suspend fun signInWithGoogle(context: Context) {
        try {

            val serverClientId = context.getString(R.string.web_client_id)
            val authRequest = createGoogleSignInRequest(serverClientId, true)

            val result = try {
                Log.d(TAG, "Attempting Google Sign-In with authorized accounts only")
                credentialManager.getCredential(
                    request = authRequest,
                    context = context
                )
            } catch (e: Exception) {
                Log.e(TAG, "Couldn't get user for auth request credentials: ${e.localizedMessage}")
                val notAuthRequest = createGoogleSignInRequest(serverClientId, false)
                credentialManager.getCredential(
                    request = notAuthRequest,
                    context = context
                )
            }

            handleGoogleLogin(result.credential)
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In error: ${e.localizedMessage}")
        }
    }

     private fun createGoogleSignInRequest(serverClientId: String, filterByAuthorizedAccounts: Boolean): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder() // already approved or auth_ed
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return request
    }

    private var currentGoogleIdToken: String? = null

    suspend fun handleGoogleLogin(credential: Credential) {
        try {
            if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                currentGoogleIdToken = googleIdTokenCredential.idToken
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            } else {
                Log.w("TAG", "Credential is not of type Google ID!")
                throw Exception("Invalid credential type")
            }
        } catch (e: Exception) {
            Log.e("TAG", "Error handling Google login: ${e.localizedMessage}")
            throw e
        }
    }

    suspend fun firebaseAuthWithGoogle(idToken: String) {
        try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()

            // Check if this is a new user or existing user
            val isNewUser = authResult.additionalUserInfo?.isNewUser == true

            if (isNewUser) {
                Log.d(TAG, "New Google user, creating patient record")
                createPatientRecordFromGoogleUser()
            } else {
                Log.d(TAG, "Existing user signed in with Google")
                // Ensure patient record exists (in case of edge cases)
                ensurePatientRecordExists()
            }

        } catch (e: Exception) {
            Log.e("TAG", "Firebase auth failed: ${e.localizedMessage}")
            throw e
        }
    }

    /*
     * NEW google login
     */
    private suspend fun createPatientRecordFromGoogleUser() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        // Check if patient record already exists
        val patientDoc = firestore.collection("patients").document(uid).get().await()
        if (patientDoc.exists()) {
            Log.d(TAG, "Patient record already exists for Google user")
            return
        }

        // Parse display name
        val fullName = user.displayName.orEmpty().split(" ", limit = 2)
        val name = fullName.getOrNull(0).orEmpty()
        val surname = fullName.getOrNull(1).orEmpty()

        val patient = Patient(
            id = uid,
            name = name,
            surname = surname,
            email = user.email.orEmpty(),
            role = Role.PATIENT
            // Other fields remain at defaults and can be filled later
        )

        firestore.collection("patients").document(uid).set(patient).await()
        Log.d(TAG, "Patient record created for Google user: $uid")
    }

    /**
     * Ensure patient record exists for current user
     */
    private suspend fun ensurePatientRecordExists() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        val patientDoc = firestore.collection("patients").document(uid).get().await()
        if (!patientDoc.exists()) {
            Log.d(TAG, "Patient record missing, creating it")
            createPatientRecordFromGoogleUser()
        }
    }


    /**
     * Check user's authentication providers
     */
    fun getUserAuthProviders(): List<String> {
        return currentUser?.providerData?.map { it.providerId } ?: emptyList()
    }

    /**
     * Check if user has both email and Google authentication
     */
    fun hasMultipleAuthMethods(): Boolean {
        val providers = getUserAuthProviders()
        return providers.contains("password") && providers.contains("google.com")
    }


    suspend fun patientRecord(): Boolean {
        val user = auth.currentUser!!
        val uid = user.uid
        val ref = firestore.collection("patients").document(uid)
        val snap = ref.get().await()

        if (!snap.exists()) {
            val fullName = user.displayName.orEmpty().split(" ", limit = 2)
            val name    = fullName.getOrNull(0).orEmpty()
            val surname = fullName.getOrNull(1).orEmpty()

            val patient = Patient(
                id          = uid,
                name        = name,
                surname     = surname,
                email       = user.email.orEmpty(),
                role        = Role.PATIENT,
                // all other fields stay at their defaults
            )
            ref.set(patient).await()
            return true // New USER patient
        }
        return false // Existing USER patient
    }

    /**
     * Check authentication providers for current user
     */
    fun checkUserAuthProviders(): AuthProvider {
        val user = currentUser ?: return AuthProvider.NOT_SIGNED_IN

        val providers = user.providerData.map { it.providerId }
        val hasEmailAuth = providers.contains("password")
        val hasGoogleAuth = providers.contains("google.com")

        return when {
            hasEmailAuth && hasGoogleAuth -> AuthProvider.BOTH_LINKED
            hasEmailAuth && !hasGoogleAuth -> AuthProvider.EMAIL_ONLY
            !hasEmailAuth && hasGoogleAuth -> AuthProvider.GOOGLE_ONLY
            else -> AuthProvider.UNKNOWN
        }
    }



    sealed class UserData {
        data class AdminData(val admin: Admin) : UserData()
        data class DoctorData(val doctor: Doctor) : UserData()
        data class PatientData(val patient: Patient) : UserData()
        object NoUser: UserData()
        object Error: UserData()
    }


    companion object {
        private const val TAG = "AuthRemoteDataSource"
    }
}