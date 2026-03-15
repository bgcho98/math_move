package com.pinkmandarin.mathmove.presentation.game

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.domain.model.PoseAction
import com.pinkmandarin.mathmove.presentation.theme.NeonGreen
import com.pinkmandarin.mathmove.presentation.theme.WrongRed

@Composable
fun AnswerChoices(
    answerChoiceActions: Map<PoseAction, Int>,
    detectedPose: PoseAction,
    poseHoldProgress: Float,
    isAnswerCorrect: Boolean?,
    showFeedback: Boolean,
    correctAnswer: Int?,
    modifier: Modifier = Modifier
) {
    val orderedActions = listOf(
        PoseAction.LEFT_HAND_UP,
        PoseAction.RIGHT_HAND_UP,
        PoseAction.LEFT_FOOT_UP,
        PoseAction.RIGHT_FOOT_UP
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // First row: Left Hand, Right Hand
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            orderedActions.take(2).forEach { action ->
                val answer = answerChoiceActions[action]
                AnswerChoiceCard(
                    action = action,
                    answer = answer,
                    isDetected = detectedPose == action,
                    holdProgress = if (detectedPose == action) poseHoldProgress else 0f,
                    isCorrect = if (showFeedback && answer == correctAnswer) true
                    else if (showFeedback && detectedPose == action && isAnswerCorrect == false) false
                    else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Second row: Left Foot, Right Foot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            orderedActions.drop(2).forEach { action ->
                val answer = answerChoiceActions[action]
                AnswerChoiceCard(
                    action = action,
                    answer = answer,
                    isDetected = detectedPose == action,
                    holdProgress = if (detectedPose == action) poseHoldProgress else 0f,
                    isCorrect = if (showFeedback && answer == correctAnswer) true
                    else if (showFeedback && detectedPose == action && isAnswerCorrect == false) false
                    else null,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AnswerChoiceCard(
    action: PoseAction,
    answer: Int?,
    isDetected: Boolean,
    holdProgress: Float,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier
) {
    val neonColor = when (action) {
        PoseAction.LEFT_HAND_UP -> Color(0xFF00BFFF)   // neon blue
        PoseAction.RIGHT_HAND_UP -> Color(0xFFBF00FF)  // neon purple
        PoseAction.LEFT_FOOT_UP -> Color(0xFF00FF7F)   // neon green
        PoseAction.RIGHT_FOOT_UP -> Color(0xFFFF6600)  // neon orange
        PoseAction.NONE -> Color.Gray
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isCorrect == true -> NeonGreen.copy(alpha = 0.15f)
            isCorrect == false -> WrongRed.copy(alpha = 0.15f)
            isDetected -> neonColor.copy(alpha = 0.15f)
            else -> Color.Black.copy(alpha = 0.2f)
        },
        animationSpec = tween(300),
        label = "bgColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isCorrect == true -> NeonGreen
            isCorrect == false -> WrongRed
            isDetected -> neonColor
            else -> neonColor.copy(alpha = 0.4f)
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = holdProgress,
        animationSpec = tween(50),
        label = "holdProgress"
    )

    // Animate between base pose → target pose
    var showTargetPose by remember { mutableStateOf(false) }
    LaunchedEffect(action) {
        while (true) {
            showTargetPose = false
            delay(700)
            showTargetPose = true
            delay(1000)
        }
    }

    val targetImageRes = when (action) {
        PoseAction.LEFT_HAND_UP,
        PoseAction.RIGHT_HAND_UP -> R.drawable.neon_human_hand_up
        PoseAction.LEFT_FOOT_UP,
        PoseAction.RIGHT_FOOT_UP -> R.drawable.neon_human_foot_up
        PoseAction.NONE -> R.drawable.neon_human_base
    }
    val mirrorX = when (action) {
        PoseAction.LEFT_HAND_UP -> true
        PoseAction.LEFT_FOOT_UP -> true
        else -> false
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isDetected) 3.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        // Fill effect: neon color fills from left to right (behind content)
        val fillFraction = if (isDetected) animatedProgress else 0f
        if (fillFraction > 0f) {
            Box(modifier = Modifier.matchParentSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fillFraction)
                        .fillMaxHeight()
                        .background(neonColor.copy(alpha = 0.25f))
                )
            }
        }

        // Content (determines parent size)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            // Character animation
            Box(
                modifier = Modifier.size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Crossfade(
                    targetState = showTargetPose,
                    animationSpec = tween(400),
                    label = "poseTransition"
                ) { isTarget ->
                    Image(
                        painter = painterResource(
                            id = if (isTarget) targetImageRes
                            else R.drawable.neon_human_base
                        ),
                        contentDescription = getPoseLabel(action),
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer(
                                scaleX = if (mirrorX) -1.8f else 1.8f,
                                scaleY = 1.8f
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Answer text next to character
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${answer ?: "?"}",
                    style = TextStyle(
                        fontSize = 52.sp,
                        fontWeight = FontWeight.ExtraBold,
                        shadow = Shadow(
                            color = neonColor,
                            offset = Offset.Zero,
                            blurRadius = 16f
                        )
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = getPoseLabel(action),
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = neonColor,
                            offset = Offset.Zero,
                            blurRadius = 8f
                        )
                    ),
                    color = neonColor
                )
            }
        }
    }
}

@Composable
private fun getPoseLabel(action: PoseAction): String {
    return when (action) {
        PoseAction.LEFT_HAND_UP -> stringResource(R.string.pose_l_hand)
        PoseAction.RIGHT_HAND_UP -> stringResource(R.string.pose_r_hand)
        PoseAction.LEFT_FOOT_UP -> stringResource(R.string.pose_l_foot)
        PoseAction.RIGHT_FOOT_UP -> stringResource(R.string.pose_r_foot)
        PoseAction.NONE -> ""
    }
}
