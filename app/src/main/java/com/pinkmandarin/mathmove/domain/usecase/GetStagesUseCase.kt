package com.pinkmandarin.mathmove.domain.usecase

import com.pinkmandarin.mathmove.domain.model.Stage
import com.pinkmandarin.mathmove.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStagesUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(userId: String): Flow<List<Stage>> {
        return gameRepository.getStages(userId)
    }
}
