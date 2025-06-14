package com.jeong.jjoreum.data.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 리뷰 정보를 저장하는 데이터 클래스
 * @param userNickname 리뷰 작성자 닉네임
 * @param userReview 리뷰 내용
 * @param userTime 리뷰 작성 시간 (Timestamp)
 * @param reviewLikeNum 좋아요(Like) 수
 * @param isLiked 현재 사용자가 좋아요를 눌렀는지 여부
 */
@Parcelize
data class ReviewItem(
    val userId: String = "",
    val userNickname: String = "",
    val userReview: String = "",
    val userTime: Long = 0L,
    var reviewLikeNum: Int = 0,
    var isLiked: Boolean = false
) : Parcelable
