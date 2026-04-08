package com.trailnote.android.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Pine700,
    onPrimary = Mist100,
    primaryContainer = Pine500,
    secondary = Lake500,
    tertiary = Sand500,
    background = Mist100,
    surface = androidx.compose.ui.graphics.Color.White,
    onBackground = Ink950,
    onSurface = Ink950,
    outline = Stone300,
    error = Danger,
)

private val DarkColors = darkColorScheme(
    primary = Pine500,
    secondary = Lake500,
    tertiary = Sand500,
)

@Composable
fun TrailNoteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = TrailNoteTypography,
        content = content,
    )
}
