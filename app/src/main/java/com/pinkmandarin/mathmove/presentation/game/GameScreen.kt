package com.pinkmandarin.mathmove.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pinkmandarin.mathmove.presentation.theme.HeartRed
import com.pinkmandarin.mathmove.presentation.theme.LockedGray
import com.pinkmandarin.mathmove.presentation.theme.NeonCyan
import com.pinkmandarin.mathmove.presentation.theme.NeonPink
import com.pinkmandarin.mathmove.presentation.theme.PrimaryOrange
import com.pinkmandarin.mathmove.presentation.theme.TimerYellow
import com.pinkmandarin.mathmove.util.Constants

@Composable
fun GameScreen(
    onGameFinished: (stageNumber: Int, correctCount: Int, totalCount: Int, timeMillis: Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate to result when game is over
    LaunchedEffect(uiState.isGameOver) {
        if (uiState.isGameOver) {
            onGameFinished(
                uiState.stageNumber,
                uiState.correctCount,
                Constants.PROBLEMS_PER_STAGE,
                uiState.totalElapsedMillis
            )
        }
    }

    val timerProgress by animateFloatAsState(
        targetValue = (uiState.timeRemainingMillis.toFloat() / uiState.totalTimeMillis).coerceIn(0f, 1f),
        animationSpec = tween(100),
        label = "timer"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview (full background)
        CameraPreview(
            onPoseDetected = { pose -> viewModel.onPoseDetected(pose) },
            modifier = Modifier.fillMaxSize()
        )

        // Game UI Overlay
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar: Stage, Timer, Lives
            GameTopBar(
                stageNumber = uiState.stageNumber,
                timeRemainingMillis = uiState.timeRemainingMillis,
                timerProgress = timerProgress,
                lives = uiState.lives,
                maxLives = Constants.INITIAL_LIVES,
                onBackClick = onBackClick
            )

            // Center: Problem Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                ProblemOverlay(
                    problem = uiState.currentProblem,
                    problemIndex = uiState.problemIndex,
                    totalProblems = uiState.totalProblems,
                    isAnswerCorrect = uiState.isAnswerCorrect,
                    showFeedback = uiState.showFeedback
                )
            }

            // Bottom: Answer Choices
            AnswerChoices(
                answerChoiceActions = uiState.answerChoiceActions,
                detectedPose = uiState.detectedPose,
                poseHoldProgress = uiState.poseHoldProgress,
                isAnswerCorrect = uiState.isAnswerCorrect,
                showFeedback = uiState.showFeedback,
                correctAnswer = uiState.currentProblem?.answer
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pose detection status bar - always visible to prevent layout shift
            PoseStatusBar(
                detectedPose = uiState.detectedPose,
                poseHoldProgress = uiState.poseHoldProgress
            )

            Spacer(modifier = Modifier.height(6.dp))
        }

        // Countdown overlay - neon style
        AnimatedVisibility(
            visible = uiState.isCountingDown,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val countdownColor = if (uiState.countdownValue > 0) NeonCyan else NeonPink
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.countdownValue > 0) "${uiState.countdownValue}" else "GO!",
                    style = TextStyle(
                        fontSize = 96.sp,
                        fontWeight = FontWeight.ExtraBold,
                        shadow = Shadow(
                            color = countdownColor,
                            offset = Offset.Zero,
                            blurRadius = 40f
                        )
                    ),
                    color = countdownColor
                )
            }
        }
    }
}

@Composable
private fun GameTopBar(
    stageNumber: Int,
    timeRemainingMillis: Long,
    timerProgress: Float,
    lives: Int,
    maxLives: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.25f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button + Stage
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Stage $stageNumber",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = NeonCyan,
                            offset = Offset.Zero,
                            blurRadius = 10f
                        )
                    ),
                    color = NeonCyan
                )
            }

            // Timer - neon glow
            val seconds = (timeRemainingMillis / 1000).toInt()
            val timerColor = when {
                seconds <= 10 -> HeartRed
                seconds <= 20 -> TimerYellow
                else -> Color.White
            }
            Text(
                text = String.format("%02d:%02d", seconds / 60, seconds % 60),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    shadow = Shadow(
                        color = timerColor,
                        offset = Offset.Zero,
                        blurRadius = 12f
                    )
                ),
                color = timerColor
            )

            // Lives (hearts)
            Row {
                repeat(maxLives) { index ->
                    Icon(
                        imageVector = if (index < lives) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (index < lives) HeartRed else LockedGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Timer progress bar - neon
        LinearProgressIndicator(
            progress = timerProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = when {
                timerProgress < 0.2f -> HeartRed
                timerProgress < 0.4f -> TimerYellow
                else -> NeonCyan
            },
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun PoseStatusBar(
    detectedPose: com.pinkmandarin.mathmove.domain.model.PoseAction,
    poseHoldProgress: Float
) {
    val poseText = when (detectedPose) {
        com.pinkmandarin.mathmove.domain.model.PoseAction.LEFT_HAND_UP -> "Left Hand Up detected!"
        com.pinkmandarin.mathmove.domain.model.PoseAction.RIGHT_HAND_UP -> "Right Hand Up detected!"
        com.pinkmandarin.mathmove.domain.model.PoseAction.LEFT_FOOT_UP -> "Left Foot Up detected!"
        com.pinkmandarin.mathmove.domain.model.PoseAction.RIGHT_FOOT_UP -> "Right Foot Up detected!"
        com.pinkmandarin.mathmove.domain.model.PoseAction.NONE -> "Move your body to answer!"
    }

    val isActive = detectedPose != com.pinkmandarin.mathmove.domain.model.PoseAction.NONE
    val statusColor = if (isActive) NeonPink else NeonCyan

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .border(
                width = 1.dp,
                color = statusColor.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = poseText,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = statusColor,
                        offset = Offset.Zero,
                        blurRadius = 8f
                    )
                ),
                color = statusColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = poseHoldProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NeonPink,
                trackColor = if (poseHoldProgress > 0f)
                    NeonPink.copy(alpha = 0.15f)
                else
                    Color.Transparent
            )
        }
    }
}
