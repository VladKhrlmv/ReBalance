package com.rebalance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Purple100,
    background = Color.Cyan,
    surface = Color.Blue,
    secondary = Blue100
)
private val DarkColors = darkColors(
    primary = Purple100,
    background = Color.Red,
    surface = Color.Yellow,
    secondary = Blue100
)

@Composable
fun PurpleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content
    )
}