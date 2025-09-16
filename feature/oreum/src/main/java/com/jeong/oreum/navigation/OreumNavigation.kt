package com.jeong.feature.oreum.navigation

object OreumNavigation {
    const val MAP = "map"
    const val LIST = "list"
    const val MY = "my"
    const val DETAIL = "detail"
    const val DETAIL_ARG = "oreum"
    const val WRITE_REVIEW = "writeReview"
    const val WRITE_REVIEW_ARG_IDX = "oreumIdx"
    const val WRITE_REVIEW_ARG_NAME = "oreumName"

    fun detailRoute(argument: String) = "$DETAIL/$argument"

    fun writeReviewRoute(idx: Int, name: String) = "$WRITE_REVIEW/$idx/$name"
}
