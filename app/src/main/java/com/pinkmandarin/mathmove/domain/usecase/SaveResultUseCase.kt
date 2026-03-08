package com.pinkmandarin.mathmove.domain.usecase

import com.pinkmandarin.mathmove.domain.model.GameResult
import com.pinkmandarin.mathmove.domain.repository.GameRepository
import javax.inject.Inject

class SaveResultUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke(userId: String, result: GameResult): Result<Unit> {
        return gameRepository.saveResult(userId, result)
    }
}
