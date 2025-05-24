package com.example.careconnect.data.datasource

import android.content.ContentValues.TAG
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import com.example.careconnect.dataclass.Admin
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

        try {
            val clearRequest = ClearCredentialStateRequest()
            credentialManager.clearCredentialState(clearRequest)
        } catch (e: ClearCredentialException) {
            Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
        }
    }

    suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

     fun googleLogin(): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId("1000314673536-jih8sqc551acbg6ev91dn6fuvjddcmar.apps.googleusercontent.com")
            .build()

        val notAuthGoogleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("1000314673536-jih8sqc551acbg6ev91dn6fuvjddcmar.apps.googleusercontent.com")
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .addCredentialOption(notAuthGoogleIdOption)
            .build()
        return request
    }

    suspend fun handleGoogleLogin(credential: Credential) {
        if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w("TAG", "Credential is not of type Google ID!")
        }
    }

    suspend fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await()
    }

    suspend fun patientRecord() {
        val user = auth.currentUser!!
        val uid = user.uid
        val ref = firestore.collection("patients").document(uid)
        val snap = ref.get().await()

        if (snap.exists()) {
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
        }

    }


    sealed class UserData {
        data class AdminData(val admin: Admin) : UserData()
        data class DoctorData(val doctor: Doctor) : UserData()
        data class PatientData(val patient: Patient) : UserData()
        object NoUser: UserData()
        object Error: UserData()
    }
}