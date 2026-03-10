package com.pinkmandarin.mathmove.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.pinkmandarin.mathmove.data.model.UserData
import com.pinkmandarin.mathmove.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getCurrentUser(): Flow<UserData?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val userData = UserData(
                    uid = firebaseUser.uid,
                    displayName = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
                trySend(userData)
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserData> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Sign in failed: user is null"))

            val userData = UserData(
                uid = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: "",
                photoUrl = firebaseUser.photoUrl?.toString()
            )

            // Save user data to Firestore
            firestore.collection("users")
                .document(userData.uid)
                .set(
                    mapOf(
                        "uid" to userData.uid,
                        "displayName" to userData.displayName,
                        "photoUrl" to userData.photoUrl,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(userData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun updateDisplayName(newName: String): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("Not logged in"))

            val uid = user.uid

            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }
            user.updateProfile(profileUpdates).await()
            user.reload().await()

            // Update Firestore user document
            firestore.collection("users")
                .document(uid)
                .update(
                    mapOf(
                        "displayName" to newName,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            // Update global ranking entry
            val globalRankRef = firestore
                .collection("rankings")
                .document("global")
                .collection("entries")
                .document(uid)
            val globalDoc = globalRankRef.get().await()
            if (globalDoc.exists()) {
                globalRankRef.update("displayName", newName).await()
            }

            // Update stage ranking entries
            val stagesSnapshot = firestore
                .collection("users")
                .document(uid)
                .collection("stages")
                .get()
                .await()

            for (stageDoc in stagesSnapshot.documents) {
                val stageNumber = stageDoc.getLong("stageNumber")?.toInt() ?: continue
                val stageRankRef = firestore
                    .collection("rankings")
                    .document("stages")
                    .collection("stage_$stageNumber")
                    .document(uid)
                val stageRankDoc = stageRankRef.get().await()
                if (stageRankDoc.exists()) {
                    stageRankRef.update("displayName", newName).await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
                ?: return Result.failure(Exception("Not logged in"))

            val uid = user.uid

            // Delete Firestore user data
            firestore.collection("users").document(uid).delete().await()

            // Delete stage records
            val stageRecords = firestore.collection("stageRecords")
                .document(uid)
                .collection("records")
                .get()
                .await()
            for (doc in stageRecords.documents) {
                doc.reference.delete().await()
            }
            firestore.collection("stageRecords").document(uid).delete().await()

            // Delete Firebase Auth account
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
