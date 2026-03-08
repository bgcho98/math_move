package com.pinkmandarin.mathmove.presentation.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pinkmandarin.mathmove.presentation.game.GameScreen
import com.pinkmandarin.mathmove.presentation.home.HomeScreen
import com.pinkmandarin.mathmove.presentation.login.LoginScreen
import com.pinkmandarin.mathmove.presentation.ranking.RankingScreen
import com.pinkmandarin.mathmove.presentation.result.ResultScreen
import com.pinkmandarin.mathmove.presentation.settings.SettingsScreen
import com.pinkmandarin.mathmove.presentation.splash.SplashScreen
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

        composable(Screen.Home.route) {
            HomeScreen(
                onStageClick = { stageNumber ->
                    navController.navigate(Screen.Game.createRoute(stageNumber))
                },
                onRankingClick = {
                    navController.navigate(Screen.Ranking.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
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
                    navController.navigate(Screen.Game.createRoute(nextStageNumber)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onRetry = { stageNumber ->
                    navController.navigate(Screen.Game.createRoute(stageNumber)) {
                        popUpTo(Screen.Home.route)
                    }
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
                }
            )
        }
    }
}
