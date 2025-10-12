package com.jeong.jejuoreum.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 앱 전반에서 공유되는 간격(Spacing) 토큰
 * 화면 간 일관된 여백과 간격을 유지하기 위해 직접적인 dp 값 대신 이 값을 사용
 */
public data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val large: Dp = 16.dp,
    val extraLarge: Dp = 20.dp,
    val extraExtraLarge: Dp = 24.dp,
    val gigantic: Dp = 32.dp,
)

private val LocalSpacing = staticCompositionLocalOf { Spacing() }

/**
 * 현재 컴포지션에 간격(Spacing) 토큰을 제공
 * 앱 테마에서 호출하여 모든 컴포저블이 간격 단위를 사용
 */

@Composable
public fun ProvideSpacing(
    spacing: Spacing = Spacing(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalSpacing provides spacing, content = content)
}

/**
 * [MaterialTheme]에서 간격(Spacing) 값을 가져오기 위한 프로퍼티
 */

public val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
