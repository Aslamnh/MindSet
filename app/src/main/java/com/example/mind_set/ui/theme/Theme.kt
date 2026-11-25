package com.example.mind_set.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MindSetColorScheme = lightColorScheme(
    primary            = MindSetPrimary,
    onPrimary          = MindSetOnPrimary,
    primaryContainer   = MindSetPrimaryContainer,
    onPrimaryContainer = MindSetNeutral,
    secondary          = MindSetSecondary,
    onSecondary        = MindSetOnSecondary,
    secondaryContainer = MindSetSecondaryContainer,
    onSecondaryContainer = MindSetNeutral,
    tertiary           = MindSetTertiary,
    onTertiary         = MindSetOnTertiary,
    tertiaryContainer  = MindSetTertiaryContainer,
    onTertiaryContainer = MindSetNeutral,
    background         = MindSetBackground,
    onBackground       = MindSetNeutral,
    surface            = MindSetSurface,
    onSurface          = MindSetNeutral,
    surfaceVariant     = MindSetCardBg,
    onSurfaceVariant   = MindSetNeutralGrey,
    error              = MindSetError,
    onError            = Color.White,
    outline            = MindSetDivider
)

@Composable
fun MindSetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MindSetColorScheme,
        typography  = Typography,
        content     = content
    )
}
