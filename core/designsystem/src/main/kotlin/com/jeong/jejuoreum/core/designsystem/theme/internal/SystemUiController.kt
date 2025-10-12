package com.jeong.jejuoreum.core.designsystem.theme.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

interface SystemUiController {
    fun setStatusBarColor(color: Color)
}

@Composable
fun rememberSystemUiController(): SystemUiController? = remember { null }
