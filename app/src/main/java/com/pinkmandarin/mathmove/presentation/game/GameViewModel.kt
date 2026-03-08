package com.pinkmandarin.mathmove.presentation.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinkmandarin.mathmove.domain.model.MathProblem
import com.pinkmandarin.mathmove.domain.model.PoseAction
import com.pinkmandarin.mathmove.domain.usecase.GenerateProblemUseCase
import com.pinkmandarin.mathmove.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val stageNumber: Int = 1,
    val currentProblem: MathProblem? = null,
    val problemIndex: Int = 0,
    val totalProblems: Int = Constants.PROBLEMS_PER_STAGE,
    val lives: Int = Constants.INITIAL_LIVES,
    val timeRemainingMillis: Long = Constants.DEFAULT_PROBLEM_TIME,
    val totalTimeMillis: Long = Constants.DEFAULT_PROBLEM_TIME,
    val correctCount: Int = 0,
    val detectedPose: PoseAction = PoseAction.NONE,
    val poseHoldProgress: Float = 0f,
    val isAnswerCorrect: Boolean? = null,
    val showFeedback: Boolean = false,
    val isGameOver: Boolean = false,
    val isGameWon: Boolean = false,
    val isCountingDown: Boolean = true,
    val countdownValue: Int = 3,
    val totalElapsedMillis: Long = 0L,
    val answerChoiceActions: Map<PoseAction, Int> = emptyMap()
)

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val generateProblemUseCase: GenerateProblemUseCase
) : ViewModel() {

    private val stageNumber: Int = savedStateHandle.get<Int>(Constants.ARG_STAGE_NUMBER) ?: 1

    private val _uiState = MutableStateFlow(GameUiState(stageNumber = stageNumber))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var problems: List<MathProblem> = emptyList()
    private var timerJob: Job? = null
    private var poseHoldJob: Job? = null
    private var currentHoldingPose: PoseAction = PoseAction.NONE
    private var gameStartTimeMillis: Long = 0L

    init {
        generateProblems()
        startCountdown()
    }

    private fun generateProblems() {
        problems = (1..Constants.PROBLEMS_PER_STAGE).map {
            generateProblemUseCase(stageNumber)
        }
        loadCurrentProblem()
    }

    private fun loadCurrentProblem() {
        val index = _uiState.value.problemIndex
        if (index < problems.size) {
            val problem = problems[index]
            val actions = listOf(
                PoseAction.LEFT_HAND_UP,
                PoseAction.RIGHT_HAND_UP,
                PoseAction.LEFT_FOOT_UP,
                PoseAction.RIGHT_FOOT_UP
            )
            val answerMap = actions.zip(problem.choices).toMap()

            val timePerProblem = calculateTimeForStage(stageNumber)
            _uiState.update {
                it.copy(
                    currentProblem = problem,
                    answerChoiceActions = answerMap,
                    timeRemainingMillis = timePerProblem,
                    totalTimeMillis = timePerProblem,
                    detectedPose = PoseAction.NONE,
                    poseHoldProgress = 0f,
                    isAnswerCorrect = null,
                    showFeedback = false
                )
            }
        }
    }

    private fun calculateTimeForStage(stage: Int): Long {
        // Decrease time as stages increase, minimum 30 seconds
        val timeMillis = Constants.DEFAULT_PROBLEM_TIME - (stage - 1) * 1000L
        return timeMillis.coerceAtLeast(Constants.MIN_PROBLEM_TIME)
    }

    private fun startCountdown() {
        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _uiState.update { it.copy(countdownValue = i) }
                delay(1000)
            }
            _uiState.update { it.copy(isCountingDown = false) }
            gameStartTimeMillis = System.currentTimeMillis()
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemainingMillis > 0) {
                delay(100)
                _uiState.update {
                    it.copy(timeRemainingMillis = it.timeRemainingMillis - 100)
                }
            }
            // Time's up - count as wrong answer
            onWrongAnswer()
        }
    }

    fun onPoseDetected(pose: PoseAction) {
        if (_uiState.value.isCountingDown || _uiState.value.showFeedback || _uiState.value.isGameOver) return

        _uiState.update { it.copy(detectedPose = pose) }

        if (pose == PoseAction.NONE) {
            cancelPoseHold()
            return
        }

        if (pose != currentHoldingPose) {
            cancelPoseHold()
            currentHoldingPose = pose
            startPoseHold(pose)
        }
    }

    private fun startPoseHold(pose: PoseAction) {
        poseHoldJob?.cancel()
        poseHoldJob = viewModelScope.launch {
            val holdDuration = Constants.POSE_HOLD_DURATION
            val updateInterval = 50L
            var elapsed = 0L

            while (elapsed < holdDuration) {
                delay(updateInterval)
                elapsed += updateInterval
                val progress = (elapsed.toFloat() / holdDuration).coerceAtMost(1f)
                _uiState.update { it.copy(poseHoldProgress = progress) }
            }

            // Pose held long enough - confirm answer
            confirmAnswer(pose)
        }
    }

    private fun cancelPoseHold() {
        poseHoldJob?.cancel()
        currentHoldingPose = PoseAction.NONE
        _uiState.update { it.copy(poseHoldProgress = 0f) }
    }

    private fun confirmAnswer(pose: PoseAction) {
        timerJob?.cancel()
        val state = _uiState.value
        val selectedAnswer = state.answerChoiceActions[pose] ?: return
        val correctAnswer = state.currentProblem?.answer ?: return

        if (selectedAnswer == correctAnswer) {
            onCorrectAnswer()
        } else {
            onWrongAnswer()
        }
    }

    private fun onCorrectAnswer() {
        _uiState.update {
            it.copy(
                isAnswerCorrect = true,
                showFeedback = true,
                correctCount = it.correctCount + 1
            )
        }
        viewModelScope.launch {
            delay(1500) // Show feedback for 1.5 seconds
            moveToNextProblem()
        }
    }

    private fun onWrongAnswer() {
        timerJob?.cancel()
        val newLives = _uiState.value.lives - 1
        _uiState.update {
            it.copy(
                isAnswerCorrect = false,
                showFeedback = true,
                lives = newLives
            )
        }

        viewModelScope.launch {
            delay(1500) // Show feedback for 1.5 seconds
            if (newLives <= 0) {
                endGame(won = false)
            } else {
                moveToNextProblem()
            }
        }
    }

    private fun moveToNextProblem() {
        val nextIndex = _uiState.value.problemIndex + 1
        if (nextIndex >= Constants.PROBLEMS_PER_STAGE) {
            endGame(won = true)
        } else {
            _uiState.update { it.copy(problemIndex = nextIndex) }
            loadCurrentProblem()
            startTimer()
        }
    }

    private fun endGame(won: Boolean) {
        val totalElapsed = System.currentTimeMillis() - gameStartTimeMillis
        _uiState.update {
            it.copy(
                isGameOver = true,
                isGameWon = won,
                totalElapsedMillis = totalElapsed
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        poseHoldJob?.cancel()
    }
}
