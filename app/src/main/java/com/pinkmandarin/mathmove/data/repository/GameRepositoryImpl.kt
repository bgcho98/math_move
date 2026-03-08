package com.pinkmandarin.mathmove.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pinkmandarin.mathmove.data.model.StageRecord
import com.pinkmandarin.mathmove.domain.model.GameResult
import com.pinkmandarin.mathmove.domain.model.Stage
import com.pinkmandarin.mathmove.domain.repository.GameRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : GameRepository {

    companion object {
        private const val TOTAL_STAGES = 60
    }

    override fun getStages(userId: String): Flow<List<Stage>> = callbackFlow {
        val listenerRegistration = firestore
            .collection("users")
            .document(userId)
            .collection("stages")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(StageRecord::class.java)
                } ?: emptyList()

                val recordMap = records.associateBy { it.stageNumber }

                val stages = (1..TOTAL_STAGES).map { number ->
                    val record = recordMap[number]
                    val isUnlocked = number == 1 || (recordMap[number - 1]?.cleared == true)
                    Stage(
                        number = number,
                        isUnlocked = isUnlocked,
                        stars = record?.stars ?: 0,
                        bestTime = record?.bestTime ?: 0L
                    )
                }

                trySend(stages)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun saveResult(userId: String, result: GameResult): Result<Unit> {
        return try {
            val stageRef = firestore
                .collection("users")
                .document(userId)
                .collection("stages")
                .document("stage_${result.stageNumber}")

            // Calculate stars:
            // 3 stars: all correct AND under time limit (10 problems * 3 seconds = 30 seconds)
            // 2 stars: all correct
            // 1 star: >= 60% correct
            val stars = calculateStars(result)

            // Get existing record to compare
            val existingDoc = stageRef.get().await()
            val existingRecord = existingDoc.toObject(StageRecord::class.java)

            // Only update if new result is better
            val shouldUpdate = existingRecord == null ||
                    stars > existingRecord.stars ||
                    (stars == existingRecord.stars && result.timeMillis < existingRecord.bestTime)

            if (shouldUpdate) {
                val newRecord = StageRecord(
                    stageNumber = result.stageNumber,
                    stars = maxOf(stars, existingRecord?.stars ?: 0),
                    bestTime = if (existingRecord != null && existingRecord.bestTime > 0L) {
                        minOf(result.timeMillis, existingRecord.bestTime)
                    } else {
                        result.timeMillis
                    },
                    correctCount = result.correctCount,
                    totalCount = result.totalCount,
                    cleared = result.cleared || (existingRecord?.cleared == true),
                    updatedAt = System.currentTimeMillis()
                )

                stageRef.set(newRecord).await()
            }

            // Update max cleared stage in user document
            if (result.cleared) {
                val userRef = firestore.collection("users").document(userId)
                val userDoc = userRef.get().await()
                val currentMax = userDoc.getLong("maxClearedStage")?.toInt() ?: 0
                if (result.stageNumber > currentMax) {
                    userRef.update("maxClearedStage", result.stageNumber).await()
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMaxClearedStage(userId: String): Flow<Int> = callbackFlow {
        val listenerRegistration = firestore
            .collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val maxStage = snapshot?.getLong("maxClearedStage")?.toInt() ?: 0
                trySend(maxStage)
            }

        awaitClose { listenerRegistration.remove() }
    }

    private fun calculateStars(result: GameResult): Int {
        if (result.correctCount < result.totalCount) return 0

        val totalMaxTime = 60_000L * result.totalCount // DEFAULT_PROBLEM_TIME per problem
        val timeRatio = result.timeMillis.toDouble() / totalMaxTime

        return when {
            timeRatio <= 0.5 -> 3
            timeRatio <= 0.75 -> 2
            else -> 1
        }
    }
}
