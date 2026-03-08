package com.pinkmandarin.mathmove.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pinkmandarin.mathmove.data.model.RankingEntry
import com.pinkmandarin.mathmove.domain.repository.RankingRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RankingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RankingRepository {

    override fun getStageRanking(stageNumber: Int, limit: Int): Flow<List<RankingEntry>> =
        callbackFlow {
            val listenerRegistration = firestore
                .collection("rankings")
                .document("stages")
                .collection("stage_$stageNumber")
                .orderBy("bestTime", Query.Direction.ASCENDING)
                .limit(limit.toLong())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val entries = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(RankingEntry::class.java)
                    } ?: emptyList()

                    trySend(entries)
                }

            awaitClose { listenerRegistration.remove() }
        }

    override fun getGlobalRanking(limit: Int): Flow<List<RankingEntry>> = callbackFlow {
        val listenerRegistration = firestore
            .collection("rankings")
            .document("global")
            .collection("entries")
            .orderBy("maxClearedStage", Query.Direction.DESCENDING)
            .orderBy("bestTime", Query.Direction.ASCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val entries = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(RankingEntry::class.java)
                } ?: emptyList()

                trySend(entries)
            }

        awaitClose { listenerRegistration.remove() }
    }
}
