//package com.jeong.jjoreum.ui.detail.review
//
//import com.jeong.jjoreum.common.OreumApplication.Companion.getUserId
//import com.jeong.jjoreum.common.OreumApplication.Companion.reviewRef
//import com.jeong.jjoreum.model.review.ReviewItem
//
///**
// * 리뷰를 Firebase에 저장하는 예시 함수
// * @param oreumIdx 오름 인덱스
// * @param userNickname 사용자 닉네임
// * @param userReview 리뷰 내용
// * @param userTime 작성 시간 (Timestamp)
// */
//fun saveReview(oreumIdx: Int, userNickname: String, userReview: String, userTime: Long) {
//    val userId = getUserId()
//    val reviewItem = ReviewItem(oreumIdx, userId, userNickname, userReview, userTime, 0)
//    reviewRef.child(oreumIdx.toString()).child(userId.toString()).setValue(reviewItem)
//}
