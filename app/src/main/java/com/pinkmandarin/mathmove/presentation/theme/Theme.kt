package com.pinkmandarin.mathmove.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MathMoveLightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = TextOnPrimary,
    primaryContainer = PrimaryOrangeLight,
    onPrimaryContainer = PrimaryOrangeDark,
    secondary = SecondaryBlue,
    onSecondary = TextOnSecondary,
    secondaryContainer = SecondaryBlueLight,
    onSecondaryContainer = SecondaryBlueDark,
    tertiary = StarGold,
    onTertiary = TextPrimary,
    tertiaryContainer = StarGoldLight,
    onTertiaryContainer = TextPrimary,
    background = BackgroundYellow,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLightGray,
    onSurfaceVariant = TextSecondary,
    error = WrongRed,
    onError = TextOnPrimary,
    errorContainer = WrongRedLight,
    onErrorContainer = TextPrimary,
    outline = LockedGray
)

@Composable
fun MathMoveTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MathMoveLightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryOrange.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MathMoveTypography,
        content = content
    )
}
