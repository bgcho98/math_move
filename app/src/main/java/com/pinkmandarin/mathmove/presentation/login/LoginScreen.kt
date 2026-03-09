package com.pinkmandarin.mathmove.presentation.login

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.pinkmandarin.mathmove.presentation.theme.TextPrimary

@Composable
fun LoginScreen(
    onGoogleSignInClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    signInResultIntent: android.content.Intent? = null,
    onSignInResultConsumed: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Process sign-in result from Activity
    LaunchedEffect(signInResultIntent) {
        signInResultIntent?.let { intent ->
            viewModel.handleGoogleSignInResult(intent)
            onSignInResultConsumed()
        }
    }

    // Gentle pulsing animation for the title
    val infiniteTransition = rememberInfiniteTransition(label = "loginAnims")
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titlePulse"
    )

    // Floating animation for emoji row
    val emojiFloat by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiFloat"
    )

    // Button bounce on idle
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonBounce"
    )

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

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

        // Semi-transparent overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.8f))

            // Big friendly welcome text
            Text(
                text = stringResource(R.string.welcome_message),
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(titleScale)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Colorful app name
            val appName = stringResource(R.string.app_name)
            Text(
                text = buildAnnotatedString {
                    val title = appName
                    val colors = listOf(
                        Color(0xFFFF6B6B),
                        Color(0xFFFFD93D),
                        Color(0xFF6BCB77),
                        Color(0xFF4D96FF),
                        Color(0xFFFF6B6B),
                        Color(0xFFAB47BC),
                        Color(0xFFFF9A6C),
                        Color(0xFF4ECDC4),
                        Color(0xFFFFD93D)
                    )
                    title.forEachIndexed { index, char ->
                        withStyle(
                            SpanStyle(
                                color = colors[index % colors.size],
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 48.sp,
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
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Fun math emoji row with floating animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.offset(y = emojiFloat.dp)
            ) {
                val mathSymbols = listOf(
                    Pair("\uD83E\uDDEE", 36), // abacus
                    Pair("\uD83D\uDD22", 36),  // input numbers
                    Pair("\u2795", 32),          // plus
                    Pair("\u2796", 32),          // minus
                    Pair("\u2716\uFE0F", 32)    // multiply
                )
                mathSymbols.forEach { (emoji, size) ->
                    Text(
                        text = emoji,
                        fontSize = size.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = stringResource(R.string.welcome_subtitle),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // White rounded card with sign-in button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(buttonScale),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.lets_get_started),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Large, child-friendly Google sign-in button
                    Button(
                        onClick = onGoogleSignInClick,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4285F4),
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFF4285F4).copy(alpha = 0.6f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Google "G" letter styled
                                Text(
                                    text = "G",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.sign_in_google),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fun encouragement text
            Text(
                text = stringResource(R.string.learn_math_fun),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = StarGold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // Error Snackbar
        uiState.errorMessage?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                containerColor = Color(0xFF2D1B69),
                contentColor = Color.White,
                action = {
                    Text(
                        text = "OK",
                        color = StarGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                },
                dismissAction = {
                    viewModel.clearError()
                }
            ) {
                Text(
                    text = error,
                    fontSize = 16.sp
                )
            }
        }
    }
}

