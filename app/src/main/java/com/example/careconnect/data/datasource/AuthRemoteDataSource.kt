package com.example.careconnect.data.datasource

import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    val currentUserFlow: Flow<Any?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                launch {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        try {
                            val user = firestore.collection("users").document(uid).get().await()
                            val role = user.getString("role")

                            val userByRole = when (role) {
                                Role.ADMIN.name -> firestore.collection("admins").document(uid).get().await()
                                Role.DOCTOR.name -> firestore.collection("doctors").document(uid).get().await()
                                Role.PATIENT.name -> firestore.collection("patients").document(uid).get().await()
                                else -> null
                            }
                            trySend(userByRole)
                        } catch (e: Exception) {
                            trySend(null)
                        }
                    } else {
                        trySend(null)
                    }
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }
    /*
    * Link authentication with email and password account (signUp)
     */
    suspend fun linkAccount(name: String, surname: String, email: String, password: String) {
        // link authentication with email and password account
        val credential = EmailAuthProvider.getCredential(email, password)
        val authResult = auth.currentUser!!.linkWithCredential(credential).await()

        // save patient id to firestore
        val userId = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        val patient = Patient(name = name, surname = surname, email = email)
        
        // Create patient document in Firestore as "patients" collection
        db.collection("user").document(userId).set(patient).await()
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount() {
        auth.currentUser!!.delete().await()
    }
}