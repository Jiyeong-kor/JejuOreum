//package com.jeong.jjoreum.ui.detail.review
//
//import com.jeong.jjoreum.common.OreumApplication
//import com.jeong.jjoreum.common.OreumApplication.Companion.myReviewRef
//import com.jeong.jjoreum.common.OreumApplication.Companion.reviewRef
//
///**
// * 다른 사용자의 리뷰에 좋아요(Like)를 설정하는 기능을 처리하는 예시 코드
// * @param oreumIdx 오름 인덱스
// * @param otherUserId 다른 사용자(리뷰 작성자)의 ID
// * @param reviewFavoriteBoolean 좋아요 여부 (true: 좋아요 누름, false: 좋아요 취소)
// * @param reviewLikeNum 기존 좋아요 수
// */
//fun saveOtherReview(
//    oreumIdx: Int,
//    otherUserId: Int,
//    reviewFavoriteBoolean: Boolean,
//    reviewLikeNum: Int
//) {
//    val myUserId = OreumApplication.getLoginSP().getInt("userId", -1).toString()
//    var reviewWholeLikeNum = reviewLikeNum
//    if (reviewFavoriteBoolean) {
//        myReviewRef.child(myUserId).child(oreumIdx.toString()).child(otherUserId.toString())
//            .setValue(reviewFavoriteBoolean.toString())
//        reviewWholeLikeNum++
//    } else {
//        myReviewRef.child(myUserId).child(oreumIdx.toString()).child(otherUserId.toString())
//            .removeValue()
//        reviewWholeLikeNum--
//    }
//    reviewRef.child(oreumIdx.toString()).child(otherUserId.toString()).child("reviewLikeNum")
//        .setValue(reviewWholeLikeNum)
//}
