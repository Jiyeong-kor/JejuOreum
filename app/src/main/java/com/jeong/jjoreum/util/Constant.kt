package com.jeong.jjoreum.util

object Constants {
    const val PREF_KEY_NICKNAME = "joinNickname"

    // Firestore Collections
    const val COLLECTION_USER_INFO = "user_info_col"
    const val COLLECTION_OREUM_INFO = "oreum_info_col"
    const val COLLECTION_REVIEWS = "reviews"
    const val SUBCOLLECTION_ITEMS = "items"

    // Firestore Fields
    const val FIELD_FAVORITES = "favorites"
    const val FIELD_STAMPED_OREUMS = "stampedOreums"
    const val FIELD_FAVORITE = "favorite"
    const val FIELD_STAMP = "stamp"
    const val FIELD_NICKNAME = "nickname"
    const val FIELD_USER_ID = "userId"
    const val FIELD_USER_NICKNAME = "userNickname"
    const val FIELD_USER_REVIEW = "userReview"
    const val FIELD_USER_TIME = "userTime"
    const val FIELD_REVIEW_LIKE_NUM = "reviewLikeNum"
    const val FIELD_IS_LIKED = "isLiked"
    const val FIELD_UID = "uid"
}
