package com.pinkmandarin.mathmove.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.presentation.theme.PrimaryOrange
import com.pinkmandarin.mathmove.presentation.theme.StarGold
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Scale animation for the title (bouncy entrance)
    val scaleAnim = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val symbolsAlpha = remember { Animatable(0f) }

    // Bouncy pulse on the title
    val infiniteTransition = rememberInfiniteTransition(label = "bounce")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleBounce"
    )

    // Floating symbols animation
    val floatOffset1 by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float1"
    )
    val floatOffset2 by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float2"
    )
    val floatOffset3 by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float3"
    )

    // Slow rotation for decorative symbols
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Loading dots animation
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )
        delay(200)
        symbolsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(500)
        )
    }

    LaunchedEffect(uiState) {
        if (!uiState.isLoading) {
            if (uiState.isLoggedIn) {
                onNavigateToHome()
            } else {
                onNavigateToLogin()
            }
        }
    }

    // Night sky gradient background
    val backgroundGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF7B2FF7), // Vibrant purple
            Color(0xFF4A1FB8), // Mid purple
            Color(0xFF1A0A5E), // Deep blue
            Color(0xFF0D0640)  // Dark navy
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
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
                .background(Color.Black.copy(alpha = 0.4f))
        )
        // Floating math symbols scattered around the screen
        FloatingSymbol(
            symbol = "+",
            color = Color(0xFFFF6B6B),
            fontSize = 40,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 40.dp, top = 120.dp)
                .offset(y = floatOffset1.dp)
                .alpha(symbolsAlpha.value * 0.7f)
                .rotate(rotation * 0.3f)
        )
        FloatingSymbol(
            symbol = "7",
            color = Color(0xFF4ECDC4),
            fontSize = 36,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 50.dp, top = 160.dp)
                .offset(y = floatOffset2.dp)
                .alpha(symbolsAlpha.value * 0.6f)
        )
        FloatingSymbol(
            symbol = "=",
            color = StarGold,
            fontSize = 44,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp)
                .offset(y = floatOffset3.dp)
                .alpha(symbolsAlpha.value * 0.5f)
                .rotate(-rotation * 0.2f)
        )
        FloatingSymbol(
            symbol = "3",
            color = Color(0xFFFF9A6C),
            fontSize = 38,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 30.dp)
                .offset(y = floatOffset1.dp)
                .alpha(symbolsAlpha.value * 0.6f)
        )
        FloatingSymbol(
            symbol = "\u00D7",
            color = Color(0xFFAB47BC),
            fontSize = 42,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 60.dp, bottom = 200.dp)
                .offset(y = floatOffset2.dp)
                .alpha(symbolsAlpha.value * 0.5f)
                .rotate(rotation * 0.25f)
        )
        FloatingSymbol(
            symbol = "5",
            color = Color(0xFF66BB6A),
            fontSize = 34,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 44.dp, bottom = 240.dp)
                .offset(y = floatOffset3.dp)
                .alpha(symbolsAlpha.value * 0.55f)
        )
        FloatingSymbol(
            symbol = "\u2212",
            color = Color(0xFF42A5F5),
            fontSize = 48,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .offset(x = 40.dp, y = floatOffset2.dp)
                .alpha(symbolsAlpha.value * 0.4f)
        )

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scaleAnim.value)
        ) {
            // Colorful "Math Move" title with each letter a different color
            Text(
                text = buildAnnotatedString {
                    val title = "Math Move"
                    val colors = listOf(
                        Color(0xFFFF6B6B), // Red
                        Color(0xFFFFD93D), // Yellow
                        Color(0xFF6BCB77), // Green
                        Color(0xFF4D96FF), // Blue
                        Color(0xFFFF6B6B), // Red (space is skipped visually)
                        Color(0xFFAB47BC), // Purple
                        Color(0xFFFF9A6C), // Orange
                        Color(0xFF4ECDC4), // Teal
                        Color(0xFFFFD93D)  // Yellow
                    )
                    title.forEachIndexed { index, char ->
                        withStyle(
                            SpanStyle(
                                color = colors[index % colors.size],
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 56.sp,
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    offset = Offset(2f, 4f),
                                    blurRadius = 6f
                                )
                            )
                        ) {
                            append(char)
                        }
                    }
                },
                modifier = Modifier.scale(titleScale)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Move your body, solve math!",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .offset(y = floatOffset1.dp * 0.3f)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Animated loading dots
            if (uiState.isLoading) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.alpha(alphaAnim.value)
                ) {
                    LoadingDot(alpha = dot1Alpha, color = Color(0xFFFF6B6B))
                    Spacer(modifier = Modifier.width(12.dp))
                    LoadingDot(alpha = dot2Alpha, color = StarGold)
                    Spacer(modifier = Modifier.width(12.dp))
                    LoadingDot(alpha = dot3Alpha, color = Color(0xFF4ECDC4))
                }
            }
        }
    }
}

@Composable
private fun FloatingSymbol(
    symbol: String,
    color: Color,
    fontSize: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = symbol,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}

@Composable
private fun LoadingDot(
    alpha: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(14.dp)
            .alpha(alpha)
            .background(color, CircleShape)
    )
}
