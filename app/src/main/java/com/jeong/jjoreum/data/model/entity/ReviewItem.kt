package com.jeong.jjoreum.data.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewItem(
    val userId: String = "",
    val userNickname: String = "",
    val userReview: String = "",
    val userTime: Long = 0L,
    val reviewLikeNum: Int = 0,
    val isLiked: Boolean = false
) : Parcelable
