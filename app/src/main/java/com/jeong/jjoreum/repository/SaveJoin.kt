package com.jeong.jjoreum.repository

fun joinSaveBtnClick1(name: String, email: String, password: String, nickname: String) {

//
//    var key: Int
//    /*joinRef.child("key").setValue(key) // firebase 첫 설정 시 사용*/
//    joinRef.child("key").get().addOnCompleteListener {
//        // firebase key 가져오기
//        key = it.result.value.toString().toInt()
//        key++
//        joinRef.child("key").setValue(key)
//        // userId 고유 key값 설정하여 저장
//        val joinItem = JoinItem(key, name, email, password, nickname, 0)
//        joinRef.child("user").child(key.toString()).setValue(joinItem)
//        // 회원가입 -> 자동로그인
//        getloginSPEditor().putInt("userId", key).commit()
//        setUserId(getLoginSP().getInt("userId", -1))
//        findNickname()
//    }.addOnFailureListener {
//        logMessage("$it")
//    }
//
//    joinRef
//    Firebase.firestore.collection()
}