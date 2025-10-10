package com.jeong.jejuoreum.core.ui.resources

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.jeong.jejuoreum.core.designsystem.R as DesignSystemR

object DesignSystemAssets {

    object Drawable {
        @DrawableRes
        val Map: Int = DesignSystemR.drawable.ic_map

        @DrawableRes
        val List: Int = DesignSystemR.drawable.ic_list

        @DrawableRes
        val ProfileSelected: Int = DesignSystemR.drawable.ic_my_selected

        @DrawableRes
        val ProfileUnselected: Int = DesignSystemR.drawable.ic_my_unselected

        @DrawableRes
        val Stamp: Int = DesignSystemR.drawable.oreum_stamp

        @DrawableRes
        val StampBadge: Int = DesignSystemR.drawable.oreum_stamp_badge

        @DrawableRes
        val StampMarkerSelected: Int = DesignSystemR.drawable.oreum_marker_selected
    }

    object String {
        @StringRes
        val MapTitle: Int = DesignSystemR.string.map_title

        @StringRes
        val ListTitle: Int = DesignSystemR.string.list_title

        @StringRes
        val ProfileTitle: Int = DesignSystemR.string.my_title
    }
}
