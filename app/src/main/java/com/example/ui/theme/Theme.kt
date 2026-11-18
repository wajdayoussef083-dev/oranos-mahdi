package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OranosDarkColorScheme = darkColorScheme(
    primary = RoyalPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = RoyalPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = ImperialGold,
    onSecondary = ObsidianBlack,
    secondaryContainer = ImperialGoldDark,
    onSecondaryContainer = ImperialGoldLight,
    tertiary = GoldAccent,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    error = ErrorRed,
    onError = Color.White
)

@Composable
fun OranosTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OranosDarkColorScheme,
        typography = Typography,
        content = content
    )
}
