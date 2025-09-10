package com.jeong.data.model.entity

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.jeong.data.FirestoreConstants

@IgnoreExtraProperties
data class JoinItem(
    val uid: String = "",
    val nickname: String = "",
    val favorites: Map<String, Boolean> = emptyMap(),
    val stampedOreums: Map<String, String> = emptyMap()
) {
    @Exclude
    fun toMap(): Map<String, Any> {
        return mapOf(
            FirestoreConstants.FIELD_UID to uid,
            FirestoreConstants.FIELD_NICKNAME to nickname,
            FirestoreConstants.FIELD_FAVORITES to favorites,
            FirestoreConstants.FIELD_STAMPED_OREUMS to stampedOreums
        )
    }
}