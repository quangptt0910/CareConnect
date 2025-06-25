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

/**
 * A remote data source class for handling authentication and user-related operations
 * using Firebase Authentication, Firebase Firestore, and Google Identity Services.
 *
 * This class supports sign-in/sign-up with email/password and Google, listens to auth state changes,
 * manages user roles, and ensures proper user data is created or merged in Firestore.
 *
 * @property auth FirebaseAuth instance for authentication.
 * @property firestore FirebaseFirestore instance for database operations.
 * @property credentialManager CredentialManager for handling Google Sign-In credentials.
 */
class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val credentialManager: CredentialManager,

) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    /**
     * Returns the UID of the currently authenticated user, or null if not signed in.
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    /**
     * Retrieves the role of the currently authenticated user from Firestore.
     *
     * @return The user's [Role] (PATIENT, DOCTOR, or ADMIN).
     * @throws Exception if user is not found in any role collection.
     */
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

    /**
     * Signs in an existing user with email and password.
     *
     * @param email User's email.
     * @param password User's password.
     */
    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Registers a new user with email/password, and creates a patient document in Firestore.
     *
     * @param name First name of the patient.
     * @param surname Last name of the patient.
     * @param email User's email.
     * @param password User's password.
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

    /**
     * Adds extra patient information to an existing account.
     *
     * @param userId User UID.
     * @param gender Gender enum as string.
     * @param weight Patient's weight.
     * @param height Patient's height.
     * @param dob Date of birth.
     * @param address Patient's address.
     */
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

    /**
     * Signs the current user out and clears credentials if using Google sign-in.
     */
    suspend fun signOut() {
        try {
            firestore.clearPersistence()
            auth.signOut()
            println("DEBUG: AuthRemote SignedOut clicked")

        // Only clear google credential of signed in with google

            val currentUser = auth.currentUser
            val googleSignIn = currentUser?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true
            if (googleSignIn || currentGoogleIdToken != null) {
                val clearRequest = ClearCredentialStateRequest(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
                credentialManager.clearCredentialState(clearRequest)
                currentGoogleIdToken = null // Clear the stored token
            }
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            auth.signOut()
            currentGoogleIdToken = null
        }
    }

    /**
     * Deletes the current user's account from Firebase Auth.
     */
    suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    /**
     * Initiates Google Sign-In flow. Falls back to unauthorized accounts if needed.
     *
     * @param context Android Context.
     */
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

    /**
     * Creates a Google Sign-In request using the provided server client ID.
     *
     * @param serverClientId OAuth 2.0 server client ID from Google Developer Console.
     * @param filterByAuthorizedAccounts Whether to only show previously authorized accounts.
     * @return A [GetCredentialRequest] for initiating Google sign-in.
     */
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

    /**
     * Handles the received [Credential] from Google Sign-In and authenticates with Firebase.
     *
     * @param credential The credential returned by the sign-in API.
     * @throws Exception if the credential type is invalid.
     */
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

    /**
     * Signs into Firebase using a Google ID token.
     * If new user, a patient record is created. If existing, it ensures the record exists.
     *
     * @param idToken Google ID token received from the credential.
     */
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

    /**
     * Creates a patient record from Google user data in Firestore.
     * Called for new users who signed up with Google.
     */
    private suspend fun createPatientRecordFromGoogleUser() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        val email = user.email ?: ""
        val patientDoc = firestore.collection("patients")
            .whereEqualTo("email", email)
            .get()
            .await()
            .documents
            .firstOrNull()

        if (patientDoc != null) {
            // Merge auth methods
            val currentMethods = patientDoc.get("authProviders") as? List<String> ?: emptyList()
            val updatedMethods = (currentMethods + "google.com").distinct()

            patientDoc.reference.update(
                "id", uid,
                "authProviders", updatedMethods
            ).await()
        } else {
            // Create new record with both methods
            val fullName = user.displayName.orEmpty().split(" ", limit = 2)
            val name = fullName.getOrNull(0).orEmpty()
            val surname = fullName.getOrNull(1).orEmpty()

            val patient = Patient(
                id = uid,
                name = name,
                surname = surname,
                email = email,
                authProviders = listOf("google.com")
            )

            firestore.collection("patients").document(uid).set(patient).await()
        }
    }

    /**
     * Ensures a patient record exists for the current Firebase user.
     * If not, it creates one using the Google user information.
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
     * Ensures a merged patient record exists for accounts using both email and Google authentication.
     */
    suspend fun ensureMergedPatientRecordExists() {
        val user = currentUser ?: return
        val uid = user.uid

        val patientRef = firestore.collection("patients").document(uid)
        if (!patientRef.get().await().exists()) {
            // Create merged record
            val fullName = user.displayName.orEmpty().split(" ", limit = 2)
            val name = fullName.getOrNull(0).orEmpty()
            val surname = fullName.getOrNull(1).orEmpty()

            val patient = Patient(
                id = uid,
                name = name,
                surname = surname,
                email = user.email ?: "",
                authProviders = listOf("password", "google.com")
            )

            patientRef.set(patient).await()
        }
    }

    /**
     * Returns a list of authentication providers linked to the current user.
     */
    fun getUserAuthProviders(): List<String> {
        return currentUser?.providerData?.map { it.providerId } ?: emptyList()
    }

    /**
     * Checks if the current user has a patient record. Creates one if missing.
     *
     * @return True if a new patient was created, false if already exists.
     */
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
     * Determines the current user's authentication method(s).
     *
     * @return [AuthProvider] indicating the method (EMAIL_ONLY, GOOGLE_ONLY, BOTH_LINKED, etc.)
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


    /**
     * Represents user data types for Admin, Doctor, Patient, or error states.
     */
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