package com.jeong.jejuoreum.core.ui.navigation

import androidx.compose.runtime.Composable
import com.jeong.jejuoreum.core.ui.resources.DesignSystemAssets

object BottomNavigationDefaults {
    object Map {
        private val visuals = BottomNavigationVisuals(
            iconRes = DesignSystemAssets.Drawable.map,
            selectedIconRes = DesignSystemAssets.Drawable.map,
            labelRes = DesignSystemAssets.Strings.mapTitle
        )

        @Composable
        fun Icon(selected: Boolean) = visuals.Icon(selected)

        @Composable
        fun Label() = visuals.Label()
    }

    object Profile {
        private val visuals = BottomNavigationVisuals(
            iconRes = DesignSystemAssets.Drawable.profileUnselected,
            selectedIconRes = DesignSystemAssets.Drawable.profileSelected,
            labelRes = DesignSystemAssets.Strings.profileTitle
        )

        @Composable
        fun Icon(selected: Boolean) = visuals.Icon(selected)

        @Composable
        fun Label() = visuals.Label()
    }
}
