package com.jeong.jejuoreum.data.remote.firestore

internal fun Any?.toStringBooleanMap(): Map<String, Boolean> =
    (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val name = key as? String
        val flag = value as? Boolean
        if (name != null && flag != null) name to flag else null
    }?.toMap() ?: emptyMap()

internal fun Any?.toStringStringMap(): Map<String, String> =
    (this as? Map<*, *>)?.mapNotNull { (key, value) ->
        val name = key as? String
        val text = value as? String
        if (name != null && text != null) name to text else null
    }?.toMap() ?: emptyMap()
