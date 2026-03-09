package com.pinkmandarin.mathmove.presentation.result

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.presentation.theme.CorrectGreen
import com.pinkmandarin.mathmove.presentation.theme.LockedGray
import com.pinkmandarin.mathmove.presentation.theme.PrimaryOrange
import com.pinkmandarin.mathmove.presentation.theme.SecondaryBlue
import com.pinkmandarin.mathmove.presentation.theme.StarGold
import com.pinkmandarin.mathmove.presentation.theme.WrongRed
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    onNextStage: (Int) -> Unit,
    onRetry: (Int) -> Unit,
    onBackToHome: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Star animations
    val starScales = List(3) { remember { Animatable(0f) } }

    LaunchedEffect(uiState.stars) {
        starScales.forEachIndexed { index, animatable ->
            if (index < uiState.stars) {
                delay(index * 400L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    // Celebration bounce for cleared stages
    val infiniteTransition = rememberInfiniteTransition(label = "celebrate")
    val celebrateBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "celebrate"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // Result Title
            Text(
                text = if (uiState.isCleared) stringResource(R.string.result_clear) else stringResource(R.string.game_over),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = if (uiState.isCleared) CorrectGreen else WrongRed,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Stage ${uiState.stageNumber}",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Stars
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star ${index + 1}",
                        tint = if (index < uiState.stars) StarGold else LockedGray.copy(alpha = 0.3f),
                        modifier = Modifier
                            .size(64.dp)
                            .scale(starScales[index].value)
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Score Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Correct / Total
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${uiState.correctCount}",
                            style = MaterialTheme.typography.displayMedium,
                            color = CorrectGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = " / ${uiState.totalCount}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }

                    Text(
                        text = stringResource(R.string.correct_answers),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Time
                    val totalSeconds = (uiState.timeMillis / 1000).toInt()
                    val minutes = totalSeconds / 60
                    val seconds = totalSeconds % 60
                    Text(
                        text = String.format(stringResource(R.string.time_format_mmss), minutes, seconds),
                        style = MaterialTheme.typography.titleLarge,
                        color = SecondaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Action Buttons
            if (uiState.isCleared) {
                Button(
                    onClick = { onNextStage(uiState.stageNumber + 1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen)
                ) {
                    Text(
                        text = stringResource(R.string.next_stage),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { onRetry(uiState.stageNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = PrimaryOrange
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = SecondaryBlue
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.go_home),
                    style = MaterialTheme.typography.titleMedium,
                    color = SecondaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
