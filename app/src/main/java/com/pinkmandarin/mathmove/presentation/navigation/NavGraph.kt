package com.pinkmandarin.mathmove.presentation.navigation

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.data.local.DailyPlayManager
import com.pinkmandarin.mathmove.presentation.game.GameScreen
import com.pinkmandarin.mathmove.presentation.home.HomeScreen
import com.pinkmandarin.mathmove.presentation.login.LoginScreen
import com.pinkmandarin.mathmove.presentation.ranking.RankingScreen
import com.pinkmandarin.mathmove.presentation.result.ResultScreen
import com.pinkmandarin.mathmove.presentation.settings.SettingsScreen
import com.pinkmandarin.mathmove.presentation.splash.SplashScreen
import com.pinkmandarin.mathmove.presentation.theme.PrimaryOrange
import com.pinkmandarin.mathmove.util.Constants

sealed class Screen(val route: String) {
    data object Splash : Screen(Constants.ROUTE_SPLASH)
    data object Login : Screen(Constants.ROUTE_LOGIN)
    data object Home : Screen(Constants.ROUTE_HOME)
    data object Game : Screen(Constants.ROUTE_GAME) {
        fun createRoute(stageNumber: Int) = "game/$stageNumber"
    }
    data object Result : Screen(Constants.ROUTE_RESULT) {
        fun createRoute(
            stageNumber: Int,
            correctCount: Int,
            totalCount: Int,
            timeMillis: Long
        ) = "result/$stageNumber/$correctCount/$totalCount/$timeMillis"
    }
    data object Ranking : Screen(Constants.ROUTE_RANKING)
    data object Settings : Screen(Constants.ROUTE_SETTINGS)
}

@Composable
fun NavGraph(
    navController: NavHostController,
    onGoogleSignInClick: () -> Unit,
    signInResultIntent: Intent? = null,
    onSignInResultConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as Activity
    val dailyPlayManager = remember { DailyPlayManager(context) }

    var showAdDialog by remember { mutableStateOf(false) }
    var pendingStageNumber by remember { mutableIntStateOf(0) }
    var isAdLoading by remember { mutableStateOf(false) }
    var remainingPlays by remember { mutableIntStateOf(dailyPlayManager.getRemainingPlays()) }

    fun navigateToGame(stageNumber: Int, popUpToHome: Boolean = false) {
        dailyPlayManager.incrementPlayCount()
        remainingPlays = dailyPlayManager.getRemainingPlays()
        if (popUpToHome) {
            navController.navigate(Screen.Game.createRoute(stageNumber)) {
                popUpTo(Screen.Home.route)
            }
        } else {
            navController.navigate(Screen.Game.createRoute(stageNumber))
        }
    }

    fun tryNavigateToGame(stageNumber: Int, popUpToHome: Boolean = false) {
        if (dailyPlayManager.canPlay()) {
            navigateToGame(stageNumber, popUpToHome)
        } else {
            pendingStageNumber = stageNumber
            showAdDialog = true
        }
    }

    fun loadAndShowRewardedAd(onRewarded: () -> Unit) {
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            Constants.AD_UNIT_ID_REWARDED_EXTRA_STAGE,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    isAdLoading = false
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            // Ad was dismissed (reward already granted via onUserEarnedReward)
                        }

                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                            Log.e("NavGraph", "Ad failed to show: ${error.message}")
                        }
                    }
                    ad.show(activity) {
                        // User earned the reward
                        onRewarded()
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoading = false
                    Log.e("NavGraph", "Rewarded ad failed to load: ${error.message}")
                    // Grant bonus anyway on load failure so user isn't stuck
                    onRewarded()
                }
            }
        )
    }

    if (showAdDialog) {
        DailyLimitDialog(
            isAdLoading = isAdLoading,
            onWatchAd = {
                loadAndShowRewardedAd {
                    showAdDialog = false
                    // Ad watched: navigate without incrementing play count (1 free game)
                    val sn = pendingStageNumber
                    navController.navigate(Screen.Game.createRoute(sn))
                }
            },
            onDismiss = {
                if (!isAdLoading) {
                    showAdDialog = false
                }
            }
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onGoogleSignInClick = onGoogleSignInClick,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                signInResultIntent = signInResultIntent,
                onSignInResultConsumed = onSignInResultConsumed
            )
        }

        composable(Screen.Home.route) { backStackEntry ->
            val nameUpdated = backStackEntry.savedStateHandle.get<Boolean>("nameUpdated") == true
            HomeScreen(
                onStageClick = { stageNumber ->
                    tryNavigateToGame(stageNumber)
                },
                onRankingClick = {
                    navController.navigate(Screen.Ranking.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                nameUpdated = nameUpdated,
                onNameUpdateConsumed = {
                    backStackEntry.savedStateHandle["nameUpdated"] = false
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument(Constants.ARG_STAGE_NUMBER) { type = NavType.IntType }
            )
        ) {
            GameScreen(
                onGameFinished = { stageNumber, correctCount, totalCount, timeMillis ->
                    navController.navigate(
                        Screen.Result.createRoute(stageNumber, correctCount, totalCount, timeMillis)
                    ) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument(Constants.ARG_STAGE_NUMBER) { type = NavType.IntType },
                navArgument(Constants.ARG_CORRECT_COUNT) { type = NavType.IntType },
                navArgument(Constants.ARG_TOTAL_COUNT) { type = NavType.IntType },
                navArgument(Constants.ARG_TIME_MILLIS) { type = NavType.LongType }
            )
        ) {
            ResultScreen(
                onNextStage = { nextStageNumber ->
                    tryNavigateToGame(nextStageNumber, popUpToHome = true)
                },
                onRetry = { stageNumber ->
                    tryNavigateToGame(stageNumber, popUpToHome = true)
                },
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Ranking.route) {
            RankingScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNameUpdated = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("nameUpdated", true)
                }
            )
        }
    }
}

@Composable
private fun DailyLimitDialog(
    isAdLoading: Boolean,
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(24.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "\uD83C\uDFAE",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.daily_limit_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.daily_limit_message),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onWatchAd,
                enabled = !isAdLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                if (isAdLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.ad_loading),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "\uD83C\uDFAC",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.daily_limit_watch_ad),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onDismiss,
                enabled = !isAdLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.daily_limit_later),
                    color = Color.Gray
                )
            }
        }
    }
}
