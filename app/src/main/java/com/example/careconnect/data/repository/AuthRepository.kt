package com.example.careconnect.data.repository

import com.example.careconnect.data.datasource.AuthRemoteDataSource
import com.example.careconnect.data.datasource.AuthRemoteDataSource.UserData
import com.example.careconnect.dataclass.Role
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    val currentUser: FirebaseUser? = authRemoteDataSource.currentUser
    val currentUserIdFlow: Flow<String?> = authRemoteDataSource.currentUserIdFlow

    val currentUserFlow: Flow<UserData> = authRemoteDataSource.currentUserFlow

    // simply login using the authRemoteDataSource with email password auth provider
    // and return the result
    suspend fun login(email: String, password: String) {
        authRemoteDataSource.login(email, password)
    }

    // signUp using the authRemoteDataSource with email password auth provider
    // link account with name and surname (Patient)
    suspend fun signUp(name: String, surname: String, email: String, password: String) {
        authRemoteDataSource.signUp(name, surname, email, password)
    }

    suspend fun linkAccount(userId: String, gender: String, weight: Double, height: Double, dob: String, address: String) {
        authRemoteDataSource.linkAccount(userId, gender, weight, height, dob, address)
    }

    suspend fun getCurrentUserRole(): Role {
        return authRemoteDataSource.getCurrentUserRole()
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }

    suspend fun deleteAccount() {
        authRemoteDataSource.deleteAccount()
    }

    fun getCurrentUserId(): String? {
        return authRemoteDataSource.getCurrentUserId()
    }


}