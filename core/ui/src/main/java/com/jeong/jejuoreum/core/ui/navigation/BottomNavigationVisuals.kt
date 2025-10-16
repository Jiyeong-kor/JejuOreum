package com.jeong.jejuoreum.core.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

class BottomNavigationVisuals(
    @param:DrawableRes private val iconRes: Int,
    @param:StringRes private val labelRes: Int,
    private val selectedIconRes: Int? = null,
) {
    @Composable
    fun Icon(selected: Boolean) {
        val painterRes = if (selected && selectedIconRes != null) {
            selectedIconRes
        } else {
            iconRes
        }
        Icon(
            painter = painterResource(painterRes),
            contentDescription = stringResource(labelRes)
        )
    }

    @Composable
    fun Label() {
        Text(text = stringResource(labelRes))
    }
}
