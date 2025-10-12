package com.jeong.jejuoreum.data.oreum.datasource.remote

import com.jeong.jejuoreum.data.oreum.di.OREUM_IMAGE_BASE_URL
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class StubOreumImageUrlResolver @Inject constructor(
    @Named(OREUM_IMAGE_BASE_URL) private val baseUrl: String,
) {

    fun resolve(relativePath: String): String {
        val normalizedBase = baseUrl.trimEnd('/')
        val normalizedPath = relativePath.trimStart('/')
        return "$normalizedBase/$normalizedPath"
    }
}
