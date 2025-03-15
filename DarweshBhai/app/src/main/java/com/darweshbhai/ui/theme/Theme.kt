package com.darweshbhai.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceLight,
    primaryContainer = SecondaryBlue,
    onPrimaryContainer = SurfaceLight,
    secondary = AccentOrange,
    onSecondary = SurfaceLight,
    secondaryContainer = AccentOrange,
    onSecondaryContainer = SurfaceLight,
    tertiary = SuccessGreen,
    onTertiary = SurfaceLight,
    tertiaryContainer = SuccessGreen,
    onTertiaryContainer = SurfaceLight,
    error = ErrorRed,
    onError = SurfaceLight,
    errorContainer = ErrorRed,
    onErrorContainer = SurfaceLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = TextSecondaryLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceDark,
    primaryContainer = SecondaryBlue,
    onPrimaryContainer = SurfaceDark,
    secondary = AccentOrange,
    onSecondary = SurfaceDark,
    secondaryContainer = AccentOrange,
    onSecondaryContainer = SurfaceDark,
    tertiary = SuccessGreen,
    onTertiary = SurfaceDark,
    tertiaryContainer = SuccessGreen,
    onTertiaryContainer = SurfaceDark,
    error = ErrorRed,
    onError = SurfaceDark,
    errorContainer = ErrorRed,
    onErrorContainer = SurfaceDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = BackgroundDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = TextSecondaryDark
)

@Composable
fun DarweshBhaiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Update status bar color
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme

            // Update system bars
            systemUiController.setSystemBarsColor(
                color = colorScheme.background,
                darkIcons = !darkTheme
            )

            // Update navigation bar
            systemUiController.setNavigationBarColor(
                color = colorScheme.background,
                darkIcons = !darkTheme
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
