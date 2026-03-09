package com.pinkmandarin.mathmove.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.domain.model.Stage
import com.pinkmandarin.mathmove.presentation.theme.BubbleLilac
import com.pinkmandarin.mathmove.presentation.theme.CandyPinkEnd
import com.pinkmandarin.mathmove.presentation.theme.CandyPinkStart
import com.pinkmandarin.mathmove.presentation.theme.CompletedGoldEnd
import com.pinkmandarin.mathmove.presentation.theme.CompletedGreenStart
import com.pinkmandarin.mathmove.presentation.theme.ElectricPurpleEnd
import com.pinkmandarin.mathmove.presentation.theme.ElectricPurpleStart
import com.pinkmandarin.mathmove.presentation.theme.GlowGold
import com.pinkmandarin.mathmove.presentation.theme.GlowPurple
import com.pinkmandarin.mathmove.presentation.theme.LockedGray
import com.pinkmandarin.mathmove.presentation.theme.LockedLavender
import com.pinkmandarin.mathmove.presentation.theme.LockedSkyMuted
import com.pinkmandarin.mathmove.presentation.theme.SparkleWhite
import com.pinkmandarin.mathmove.presentation.theme.StarGold
import com.pinkmandarin.mathmove.presentation.theme.TextOnPrimary

@Composable
fun StageItem(
    stage: Stage,
    isCurrentStage: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLocked = !stage.isUnlocked
    val isCompleted = stage.stars > 0

    val infiniteTransition = rememberInfiniteTransition(label = "stageAnim")

    // Bouncing pulse for current stage
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrentStage) 1.07f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "stagePulse"
    )

    // Sparkle rotation for current stage
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRot"
    )

    // Star bounce for completed stages
    val starBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starBounce"
    )

    // Glow pulse for current stage
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    val cardGradient = when {
        isLocked -> Brush.linearGradient(
            colors = listOf(
                LockedLavender.copy(alpha = 0.5f),
                LockedSkyMuted.copy(alpha = 0.5f)
            )
        )
        isCompleted -> Brush.linearGradient(
            colors = listOf(CompletedGreenStart, CompletedGoldEnd)
        )
        isCurrentStage -> Brush.linearGradient(
            colors = listOf(ElectricPurpleStart, CandyPinkStart)
        )
        else -> Brush.linearGradient(
            colors = listOf(ElectricPurpleEnd.copy(alpha = 0.6f), BubbleLilac.copy(alpha = 0.7f))
        )
    }

    val shadowElevation = when {
        isCurrentStage -> 12.dp
        isCompleted -> 8.dp
        isLocked -> 2.dp
        else -> 6.dp
    }

    val itemScale = if (isLocked) 0.92f else pulseScale

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(itemScale),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect behind current stage
        if (isCurrentStage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.15f)
                    .alpha(glowAlpha)
                    .clip(RoundedCornerShape(28.dp))
                    .background(GlowPurple)
            )
        }
        // Glow effect behind completed stage
        if (isCompleted && !isCurrentStage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.08f)
                    .alpha(0.4f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(GlowGold)
            )
        }

        // Main card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = shadowElevation,
                    shape = RoundedCornerShape(24.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(24.dp))
                .background(cardGradient)
                .then(
                    if (!isLocked) Modifier.clickable { onClick() }
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                // ===== LOCKED STATE =====
                isLocked -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.6f)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = LockedGray,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stage.number}",
                            color = LockedGray,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // ===== COMPLETED STATE =====
                isCompleted && !isCurrentStage -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Checkmark in top-right
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Completed",
                            tint = TextOnPrimary.copy(alpha = 0.9f),
                            modifier = Modifier
                                .size(22.dp)
                                .align(Alignment.TopEnd)
                                .padding(top = 6.dp, end = 6.dp)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            // Stage number - large and bold
                            Text(
                                text = "${stage.number}",
                                color = TextOnPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Golden animated stars
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                repeat(3) { index ->
                                    val earned = index < stage.stars
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (earned) StarGold else TextOnPrimary.copy(alpha = 0.3f),
                                        modifier = Modifier
                                            .size(24.dp)
                                            .then(
                                                if (earned) Modifier.offset(y = starBounce.dp)
                                                else Modifier
                                            )
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // ===== CURRENT (UNLOCKED, ACTIVE) STAGE =====
                isCurrentStage -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Sparkle decorations
                        Text(
                            text = "\u2728",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(6.dp)
                                .rotate(sparkleRotation)
                        )
                        Text(
                            text = "\u2728",
                            fontSize = 12.sp,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(6.dp)
                                .rotate(-sparkleRotation)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            // Glowing number circle
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                SparkleWhite.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                    .background(
                                        SparkleWhite.copy(alpha = 0.25f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stage.number}",
                                    color = TextOnPrimary,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // "GO!" badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            colors = listOf(CandyPinkStart, CandyPinkEnd)
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.go),
                                    color = TextOnPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // ===== UNLOCKED BUT NOT YET PLAYED =====
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${stage.number}",
                            color = TextOnPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // Empty star placeholders
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            repeat(3) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = TextOnPrimary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
