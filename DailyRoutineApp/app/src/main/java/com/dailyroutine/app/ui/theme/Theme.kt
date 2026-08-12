package com.dailyroutine.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Surface,
    primaryContainer = TealSoft,
    onPrimaryContainer = TealDeep,
    secondary = Orange,
    onSecondary = Surface,
    secondaryContainer = OrangeSoft,
    onSecondaryContainer = OrangeDeep,
    background = Background,
    onBackground = Ink,
    surface = Surface,
    onSurface = Ink,
    surfaceVariant = Background,
    onSurfaceVariant = InkMuted,
    outline = Border,
    error = Red,
)

@Composable
fun DailyRoutineTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = AppTypography,
        content = content,
    )
}
