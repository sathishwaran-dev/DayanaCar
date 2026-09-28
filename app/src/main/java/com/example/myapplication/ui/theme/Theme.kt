package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenContainer,
    onPrimaryContainer = GreenOnContainer,
    secondary = SecondaryColor,
    secondaryContainer = SecondaryContainer,
    background = LightBackground,
    surface = LightSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = OutlineColor
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF1E6B38),
    onPrimaryContainer = Color(0xFFE8F5E9),
    secondary = Color(0xFFA5D6A7),
    secondaryContainer = Color(0xFF2E3D31),
    background = Color(0xFF121412),
    surface = Color(0xFF1E211E),
    onBackground = Color(0xFFE2E3E0),
    onSurface = Color(0xFFE2E3E0),
    outline = Color(0xFF8C938C)
)

@Composable
fun DayanaCarTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
