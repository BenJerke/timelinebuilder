package dev.benj.timelinebuilder.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TimelineNeutralLight,        // Light stone for primary elements
    secondary = TimelineAccent2,           // Sage green for secondary
    tertiary = TimelineAccent1,            // Muted plum for tertiary
    background = TimelineNeutralDark,      // Almost black background
    surface = Color(0xFF1A1A1A),           // Slightly lighter than background
    error = TimelineTertiary,              // Soft clay for errors in dark mode
    onPrimary = TimelineNeutralDark,       // Dark text on light primary
    onSecondary = Color.White,             // White text on sage secondary
    onTertiary = Color.White,              // White text on muted plum tertiary
    onBackground = TimelineNeutralLight,   // Light stone text on dark background
    onSurface = TimelineNeutralLight,      // Light stone text on dark surface
)

private val LightColorScheme = lightColorScheme(
    primary = TimelinePrimary,
    secondary = TimelineSecondary,
    tertiary = TimelineTertiary,
    background = TimelineBackground,
    surface = TimelineSurface,
    error = TimelineError,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun TimelineBuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
