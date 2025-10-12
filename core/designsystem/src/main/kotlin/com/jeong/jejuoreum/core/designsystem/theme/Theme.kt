package com.jeong.jejuoreum.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.jeong.jejuoreum.core.designsystem.theme.internal.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    secondary = SkyBlue,
    tertiary = DeepBlue,
    background = SnowWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = WarmGray
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    secondary = DeepBlue,
    tertiary = ForestGreen,
    background = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = SnowWhite
)

@Composable
fun JejuOreumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController?.setStatusBarColor(colorScheme.primary)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JejuOreumTypography,
        content = content
    )
}
