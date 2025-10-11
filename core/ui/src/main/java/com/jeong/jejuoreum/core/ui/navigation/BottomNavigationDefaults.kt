package com.jeong.jejuoreum.core.ui.navigation

import androidx.compose.runtime.Composable
import com.jeong.jejuoreum.core.ui.resources.DesignSystemAssets

object BottomNavigationDefaults {
    object Map {
        private val visuals = BottomNavigationVisuals(
            iconRes = DesignSystemAssets.Icon.HomeUnselected,
            selectedIconRes = DesignSystemAssets.Icon.HomeSelected,
            labelRes = DesignSystemAssets.String.HomeTitle
        )

        @Composable
        fun Icon(selected: Boolean) = visuals.Icon(selected)

        @Composable
        fun Label() = visuals.Label()
    }

    object Profile {
        private val visuals = BottomNavigationVisuals(
            iconRes = DesignSystemAssets.Icon.ProfileUnselected,
            selectedIconRes = DesignSystemAssets.Icon.ProfileSelected,
            labelRes = DesignSystemAssets.String.ProfileTitle
        )

        @Composable
        fun Icon(selected: Boolean) = visuals.Icon(selected)

        @Composable
        fun Label() = visuals.Label()
    }
}
