package com.pinkmandarin.mathmove.domain.usecase

import com.pinkmandarin.mathmove.data.model.RankingEntry
import com.pinkmandarin.mathmove.domain.repository.RankingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRankingUseCase @Inject constructor(
    private val rankingRepository: RankingRepository
) {

    fun stageRanking(stageNumber: Int, limit: Int = 20): Flow<List<RankingEntry>> {
        return rankingRepository.getStageRanking(stageNumber, limit)
    }

    fun globalRanking(limit: Int = 20): Flow<List<RankingEntry>> {
        return rankingRepository.getGlobalRanking(limit)
    }
}
