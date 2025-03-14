package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.dataclass.Patient
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    val currentUser: FirebaseUser? = authRemoteDataSource.currentUser
    val currentUserIdFlow: Flow<String?> = authRemoteDataSource.currentUserIdFlow

    // simply login using the authRemoteDataSource with email password auth provider
    // and return the result
    suspend fun login(email: String, password: String) {
        authRemoteDataSource.login(email, password)
    }

    // signUp using the authRemoteDataSource with email password auth provider
    // only the patient would signUp and have patient data class
    suspend fun signUp(email: String, password: String, patient: Patient) {
        authRemoteDataSource.linkAccount(email, password, patient)
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }

    suspend fun deleteAccount() {
        authRemoteDataSource.deleteAccount()
    }
}