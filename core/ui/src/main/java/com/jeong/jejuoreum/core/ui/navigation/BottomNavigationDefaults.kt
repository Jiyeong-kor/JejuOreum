package com.jeong.jejuoreum.core.ui.navigation

import com.jeong.jejuoreum.core.ui.resources.DesignSystemAssets

object BottomNavigationDefaults {
    val items = listOf(
        BottomNavItem(
            labelRes = DesignSystemAssets.String.HomeTitle,
            iconSelected = DesignSystemAssets.Icon.HomeSelected,
            iconUnselected = DesignSystemAssets.Icon.HomeUnselected
        ),
        BottomNavItem(
            labelRes = DesignSystemAssets.String.ProfileTitle,
            iconSelected = DesignSystemAssets.Icon.ProfileSelected,
            iconUnselected = DesignSystemAssets.Icon.ProfileUnselected
        )
    )
}
