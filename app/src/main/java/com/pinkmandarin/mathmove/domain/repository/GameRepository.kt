package com.pinkmandarin.mathmove.domain.repository

import com.pinkmandarin.mathmove.domain.model.GameResult
import com.pinkmandarin.mathmove.domain.model.Stage
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getStages(userId: String): Flow<List<Stage>>
    suspend fun saveResult(userId: String, result: GameResult): Result<Unit>
    fun getMaxClearedStage(userId: String): Flow<Int>
}
