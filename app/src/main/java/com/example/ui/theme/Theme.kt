package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NexoraDarkColorScheme = darkColorScheme(
    primary = NexoraCyan,
    onPrimary = NexoraDarkBg,
    primaryContainer = NexoraSurfaceVariant,
    onPrimaryContainer = NexoraCyan,
    secondary = NexoraPurple,
    onSecondary = Color.White,
    secondaryContainer = NexoraSurfaceElevated,
    onSecondaryContainer = NexoraTextPrimary,
    tertiary = NexoraPurple,
    background = NexoraDarkBg,
    onBackground = NexoraTextPrimary,
    surface = NexoraSurface,
    onSurface = NexoraTextPrimary,
    surfaceVariant = NexoraSurfaceVariant,
    onSurfaceVariant = NexoraTextSecondary,
    outline = NexoraBorder,
    error = NexoraError,
    onError = Color.White
)

private val NexoraLightColorScheme = lightColorScheme(
    primary = Color(0xFF007799),
    onPrimary = Color.White,
    secondary = Color(0xFF5B4BCC),
    background = Color(0xFFF4F7FC),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0B1020),
    onSurface = Color(0xFF0B1020),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626)
)

@Composable
fun NexoraTheme(
    darkTheme: Boolean = true, // Gaming UI defaults to Dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NexoraDarkColorScheme else NexoraLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    NexoraTheme(darkTheme = darkTheme, content = content)
}
