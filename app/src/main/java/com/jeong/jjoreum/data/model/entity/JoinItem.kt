package com.jeong.jjoreum.data.model.entity

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.jeong.jjoreum.util.Constants

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
            Constants.FIELD_UID to uid,
            Constants.FIELD_NICKNAME to nickname,
            Constants.FIELD_FAVORITES to favorites,
            Constants.FIELD_STAMPED_OREUMS to stampedOreums
        )
    }
}
