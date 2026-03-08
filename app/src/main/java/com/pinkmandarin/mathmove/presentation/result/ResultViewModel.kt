package com.pinkmandarin.mathmove.presentation.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinkmandarin.mathmove.domain.model.GameResult
import com.pinkmandarin.mathmove.domain.repository.AuthRepository
import com.pinkmandarin.mathmove.domain.usecase.SaveResultUseCase
import com.pinkmandarin.mathmove.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val stageNumber: Int = 1,
    val correctCount: Int = 0,
    val totalCount: Int = Constants.PROBLEMS_PER_STAGE,
    val timeMillis: Long = 0L,
    val stars: Int = 0,
    val isCleared: Boolean = false,
    val isSaving: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveResultUseCase: SaveResultUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val stageNumber: Int = savedStateHandle.get<Int>(Constants.ARG_STAGE_NUMBER) ?: 1
    private val correctCount: Int = savedStateHandle.get<Int>(Constants.ARG_CORRECT_COUNT) ?: 0
    private val totalCount: Int = savedStateHandle.get<Int>(Constants.ARG_TOTAL_COUNT) ?: Constants.PROBLEMS_PER_STAGE
    private val timeMillis: Long = savedStateHandle.get<Long>(Constants.ARG_TIME_MILLIS) ?: 0L

    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState: StateFlow<ResultUiState> = _uiState.asStateFlow()

    init {
        calculateAndSaveResult()
    }

    private fun calculateAndSaveResult() {
        val isCleared = correctCount == totalCount
        val stars = calculateStars(correctCount, totalCount, timeMillis)

        _uiState.update {
            it.copy(
                stageNumber = stageNumber,
                correctCount = correctCount,
                totalCount = totalCount,
                timeMillis = timeMillis,
                stars = stars,
                isCleared = isCleared,
                isSaving = true
            )
        }

        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser().first()
                    ?: throw Exception("Not logged in")

                val result = GameResult(
                    stageNumber = stageNumber,
                    correctCount = correctCount,
                    totalCount = totalCount,
                    timeMillis = timeMillis,
                    stars = stars,
                    cleared = isCleared
                )
                saveResultUseCase(user.uid, result)
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.message ?: "Failed to save result"
                    )
                }
            }
        }
    }

    private fun calculateStars(correct: Int, total: Int, timeMillis: Long): Int {
        if (correct < total) return 0

        val maxTimePerProblem = Constants.DEFAULT_PROBLEM_TIME
        val totalMaxTime = maxTimePerProblem * total
        val timeRatio = timeMillis.toDouble() / totalMaxTime

        return when {
            timeRatio <= Constants.THREE_STAR_TIME_FACTOR -> 3
            timeRatio <= Constants.TWO_STAR_TIME_FACTOR -> 2
            else -> 1
        }
    }
}
