package com.jeong.jjoreum.repository

internal fun Any?.toStringBooleanMap(): Map<String, Boolean> =
    (this as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = v as? Boolean
        if (key != null && value != null) key to value else null
    }?.toMap() ?: emptyMap()

internal fun Any?.toStringStringMap(): Map<String, String> =
    (this as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = v as? String
        if (key != null && value != null) key to value else null
    }?.toMap() ?: emptyMap()