package com.pinkmandarin.mathmove.presentation.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.domain.model.MathProblem
import com.pinkmandarin.mathmove.presentation.theme.CorrectGreen
import com.pinkmandarin.mathmove.presentation.theme.NeonCyan
import com.pinkmandarin.mathmove.presentation.theme.NeonGreen
import com.pinkmandarin.mathmove.presentation.theme.NeonPink
import com.pinkmandarin.mathmove.presentation.theme.WrongRed

@Composable
fun ProblemOverlay(
    problem: MathProblem?,
    problemIndex: Int,
    totalProblems: Int,
    isAnswerCorrect: Boolean?,
    showFeedback: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Problem counter - neon style
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = NeonCyan.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Q${problemIndex + 1} / $totalProblems",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = NeonCyan,
                            offset = Offset.Zero,
                            blurRadius = 12f
                        )
                    ),
                    color = NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Math problem - neon glow text, transparent background
            if (problem != null) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = NeonPink.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 32.dp, vertical = 32.dp)
                ) {
                    Text(
                        text = problem.questionText,
                        style = TextStyle(
                            fontSize = 56.sp,
                            fontWeight = FontWeight.ExtraBold,
                            shadow = Shadow(
                                color = NeonPink,
                                offset = Offset.Zero,
                                blurRadius = 32f
                            )
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Answer feedback - neon glow
            AnimatedVisibility(
                visible = showFeedback,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                val feedbackColor = if (isAnswerCorrect == true) NeonGreen else WrongRed
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = feedbackColor.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (isAnswerCorrect == true) stringResource(R.string.correct) else stringResource(R.string.wrong),
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            shadow = Shadow(
                                color = feedbackColor,
                                offset = Offset.Zero,
                                blurRadius = 16f
                            )
                        ),
                        color = feedbackColor
                    )
                }
            }
        }
    }
}
