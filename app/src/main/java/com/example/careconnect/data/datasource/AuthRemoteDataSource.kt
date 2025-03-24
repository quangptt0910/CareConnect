package com.example.careconnect.data.datasource

import android.util.Log
import com.example.careconnect.dataclass.Admin
import com.example.careconnect.dataclass.Doctor
import com.example.careconnect.dataclass.Patient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private val firestore: FirebaseFirestore
) {

    val currentUser: FirebaseUser? get() = auth.currentUser

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

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }

    public sealed class UserData {
        data class AdminData(val admin: Admin) : UserData()
        data class DoctorData(val doctor: Doctor) : UserData()
        data class PatientData(val patient: Patient) : UserData()
        object NoUser: UserData()
        object Error: UserData()
    }
}