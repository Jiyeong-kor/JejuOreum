package com.jeong.jejuoreum.core.navigation

import android.net.Uri

object OreumNavigation {

    object Splash {
        const val ROUTE: String = "oreum/splash"
    }

    object Onboarding {
        const val ROUTE: String = "oreum/onboarding"
    }

    object Map {
        const val ROUTE: String = "oreum/map"

        object SavedStateKeys {
            const val CAMERA_LATITUDE: String = "oreum_map_camera_lat"
            const val CAMERA_LONGITUDE: String = "oreum_map_camera_lon"
            const val CAMERA_ZOOM: String = "oreum_map_camera_zoom"
        }
    }

    object Profile {
        const val ROUTE: String = "oreum/my"
    }

    object Detail {
        const val ROUTE: String = "oreum/detail"
        const val INITIAL_OREUM_KEY: String = "oreum_detail_initial"
    }

    object Review {
        const val ROUTE: String = "oreum/writeReview"
        const val ARG_OREUM_ID: String = "oreum_write_review_arg_idx"
        const val ARG_OREUM_NAME: String = "oreum_write_review_arg_name"

        fun route(oreumId: Int, oreumName: String): String {
            val encodedName = Uri.encode(oreumName)
            return "$ROUTE/$oreumId/$encodedName"
        }
    }
}
