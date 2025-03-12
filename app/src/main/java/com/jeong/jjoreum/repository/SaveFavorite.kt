//package com.jeong.jjoreum.ui.detail.favorite
//
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.ViewModelStoreOwner
//
///**
// * 즐겨찾기(Favorite) 기능을 처리하는 함수 예시 (Firebase Realtime DB 사용)
// * @param favoriteBoolean 좋아요를 누른 상태인지 여부
// * @param oreumIdx 오름 인덱스
// * @param owner ViewModelStoreOwner (예: 액티비티, 프래그먼트)
// */
//
//fun saveFavorite(favoriteBoolean: Boolean, oreumIdx: Int, owner: ViewModelStoreOwner) {
//    val userId = getLoginSP().getInt("userId", -1).toString()
//    var oreumFavorite: MutableList<OreumWholeFavorite>
//    var favoriteNum: Int
//    val favoriteViewModel: FavoriteViewModel =
//        ViewModelProvider(owner).get(FavoriteViewModel::class.java)
//
//    // addOnCompleteListener -> Firebase Realtime Database에서 데이터를 비동기적으로 가져오는 작업을 수행할 때 사용되는 콜백 리스너
//    oreumRef.child(oreumIdx.toString()).child("favorite").get().addOnCompleteListener {
//        favoriteNum = it.result.value.toString().toInt()
//        oreumFavorite = getOreumFavorite()
//
//        val favoriteItem = MyFavoriteItem(userId.toInt(), oreumIdx, favoriteBoolean)
//
//        // 좋아요 버튼을 클릭했을 때 동작 구현 favoriteBoolean이 true면 클릭한 거니까
//        // 내 favorite에 setValue하고 favoriteNum +1 함
//        if (favoriteBoolean) {
//            favoriteRef.child(userId).child(oreumIdx.toString())
//                .setValue(favoriteItem) // my favorite 변경
//            favoriteNum++ // 전체 favorite 변경
//        } else { // favoriteBoolean이 false면 좋아요를 취소한 거임
//            favoriteRef.child(userId).child(oreumIdx.toString()).removeValue() // my favorite 변경
//            favoriteNum-- // 전체 favorite 변경
//        }
//        oreumRef.child(oreumIdx.toString()).child("favorite")
//            .setValue(favoriteNum) // firebase 해당 오름 favorite 숫자 변경
//        oreumFavorite[oreumIdx].oreumFavorite = favoriteNum // Application 오름 list 변경 -1
//        setOreumFavortie(oreumFavorite) // Application 오름 list 변경 -2
//        favoriteViewModel.changeFavoriteListLiveDate(oreumFavorite) // favorite viewModel 변경
//    }
//}
