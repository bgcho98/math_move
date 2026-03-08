package com.pinkmandarin.mathmove.domain.repository

import com.pinkmandarin.mathmove.data.model.RankingEntry
import kotlinx.coroutines.flow.Flow

interface RankingRepository {
    fun getStageRanking(stageNumber: Int, limit: Int = 20): Flow<List<RankingEntry>>
    fun getGlobalRanking(limit: Int = 20): Flow<List<RankingEntry>>
}
