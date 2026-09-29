package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FirebaseAmber,
    onPrimary = Color.Black,
    primaryContainer = FirebaseAmberDark,
    onPrimaryContainer = Color.White,
    secondary = AgentCyan,
    onSecondary = Color.Black,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = AgentCyanLight,
    tertiary = FirebaseYellow,
    background = DeepDarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkSurfaceBorder,
    error = ErrorRed,
)

private val LightColorScheme = lightColorScheme(
    primary = FirebaseAmberDark,
    onPrimary = Color.White,
    secondary = AgentCyanDark,
    onSecondary = Color.White,
    tertiary = FirebaseAmber,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightBackground,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightSurfaceBorder,
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek Developer Console Dark Theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
