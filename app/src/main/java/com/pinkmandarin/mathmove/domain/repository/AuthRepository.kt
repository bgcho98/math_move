package com.pinkmandarin.mathmove.domain.repository

import com.pinkmandarin.mathmove.data.model.UserData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getCurrentUser(): Flow<UserData?>
    suspend fun signInWithGoogle(idToken: String): Result<UserData>
    suspend fun signOut()
    suspend fun updateDisplayName(newName: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
}
