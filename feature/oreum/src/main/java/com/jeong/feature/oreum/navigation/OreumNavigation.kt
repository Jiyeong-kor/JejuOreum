package com.jeong.feature.oreum.navigation

import android.net.Uri

object OreumNavigation {
    const val MAP: String = "oreum/map"
    const val LIST: String = "oreum/list"
    const val MY: String = "oreum/my"
    const val DETAIL: String = "oreum/detail"
    const val DETAIL_OREUM_KEY: String = "oreum_detail_initial"
    const val WRITE_REVIEW: String = "oreum/writeReview"
    const val WRITE_REVIEW_ARG_IDX: String = "oreum_write_review_arg_idx"
    const val WRITE_REVIEW_ARG_NAME: String = "oreum_write_review_arg_name"

    fun writeReviewRoute(idx: Int, name: String): String {
        val encodedName = Uri.encode(name)
        return "$WRITE_REVIEW/$idx/$encodedName"
    }
}
