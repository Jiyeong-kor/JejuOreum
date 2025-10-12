package com.jeong.jejuoreum.core.ui.resources

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jeong.jejuoreum.core.designsystem.R as DesignSystemR

object DesignSystemAssets {

    object Drawable {
        @DrawableRes
        val map: Int = DesignSystemR.drawable.ic_map

        @DrawableRes
        val list: Int = DesignSystemR.drawable.ic_list

        @DrawableRes
        val profileSelected: Int = DesignSystemR.drawable.ic_my_selected

        @DrawableRes
        val profileUnselected: Int = DesignSystemR.drawable.ic_my_unselected

        @DrawableRes
        val stamp: Int = DesignSystemR.drawable.oreum_stamp

        @DrawableRes
        val stampBadge: Int = DesignSystemR.drawable.oreum_stamp_badge

        @DrawableRes
        val stampMarkerSelected: Int = DesignSystemR.drawable.oreum_marker_selected
    }

    object Strings {
        @StringRes
        val mapTitle: Int = DesignSystemR.string.map_title

        @StringRes
        val listTitle: Int = DesignSystemR.string.list_title

        @StringRes
        val profileTitle: Int = DesignSystemR.string.my_title
    }
}
