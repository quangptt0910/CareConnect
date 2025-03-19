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
    // link account with name and surname (Patient)
    suspend fun signUp(name: String, surname: String, email: String, password: String) {
        authRemoteDataSource.linkAccount(name, surname, email, password)
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }

    suspend fun deleteAccount() {
        authRemoteDataSource.deleteAccount()
    }
}