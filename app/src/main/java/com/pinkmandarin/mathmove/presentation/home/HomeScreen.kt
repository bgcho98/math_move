package com.pinkmandarin.mathmove.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.presentation.theme.AvatarRingEnd
import com.pinkmandarin.mathmove.presentation.theme.AvatarRingStart
import com.pinkmandarin.mathmove.presentation.theme.BubbleLemon
import com.pinkmandarin.mathmove.presentation.theme.BubblePeach
import com.pinkmandarin.mathmove.presentation.theme.CandyPinkStart
import com.pinkmandarin.mathmove.presentation.theme.ElectricPurpleEnd
import com.pinkmandarin.mathmove.presentation.theme.ElectricPurpleStart
import com.pinkmandarin.mathmove.presentation.theme.GradientPurpleEnd
import com.pinkmandarin.mathmove.presentation.theme.GradientPurpleStart
import com.pinkmandarin.mathmove.presentation.theme.SparkleWhite
import com.pinkmandarin.mathmove.presentation.theme.StarGold
import com.pinkmandarin.mathmove.presentation.theme.TextOnPrimary

@Composable
fun HomeScreen(
    onStageClick: (Int) -> Unit,
    onRankingClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Sparkle rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "homeSparkle")
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkleRotation"
    )
    val starBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starBounce"
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            GradientPurpleStart,
            GradientPurpleEnd,
            GradientPurpleEnd.copy(alpha = 0.7f)
        )
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.first_screen_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Dark overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(56.dp)
                    .align(Alignment.Center),
                color = StarGold,
                strokeWidth = 5.dp
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ===== Fun Top Header =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    ElectricPurpleStart,
                                    ElectricPurpleEnd
                                )
                            ),
                            shape = RoundedCornerShape(
                                bottomStart = 32.dp,
                                bottomEnd = 32.dp
                            )
                        )
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(
                                bottomStart = 32.dp,
                                bottomEnd = 32.dp
                            ),
                            clip = false
                        )
                        .padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 48.dp,
                            bottom = 20.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar with Google profile photo
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .border(
                                    width = 3.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            AvatarRingStart,
                                            AvatarRingEnd
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            CandyPinkStart,
                                            BubblePeach
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.userPhotoUrl != null) {
                                AsyncImage(
                                    model = uiState.userPhotoUrl,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = uiState.userName.firstOrNull()?.uppercase() ?: "?",
                                    color = TextOnPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Name + star count
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.userName,
                                color = TextOnPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = StarGold,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .offset(y = starBounce.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.totalStars}",
                                    color = BubbleLemon,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "\u2728",
                                    fontSize = 14.sp,
                                    modifier = Modifier.rotate(sparkleRotation)
                                )
                            }
                        }

                        // Ranking button
                        IconButton(
                            onClick = onRankingClick,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SparkleWhite.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Ranking",
                                tint = StarGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Settings button
                        IconButton(
                            onClick = onSettingsClick,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(SparkleWhite.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ===== Adventure subtitle =====
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\u2B50",
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.stage_waiting, uiState.maxClearedStage + 1),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\u2B50",
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ===== Stage Grid (3 columns) =====
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.stages,
                        key = { it.number }
                    ) { stage ->
                        StageItem(
                            stage = stage,
                            isCurrentStage = stage.number == uiState.maxClearedStage + 1,
                            onClick = { onStageClick(stage.number) }
                        )
                    }
                }
            }
        }
    }
}
