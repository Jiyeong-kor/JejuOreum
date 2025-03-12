package com.jeong.jjoreum.data.model.api

import android.annotation.SuppressLint
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Retrofit과 OkHttpClient를 설정하는 객체
 */
object RetrofitOkHttpManager {

    private const val BASE_URL = "https://gis.jeju.go.kr/rest/JejuOleumVRImg/"

    // SSL 인증서 우회 클라이언트 사용
    private val okHttpClient: OkHttpClient = getUnsafeOkHttpClient()

    /**
     * Retrofit 빌더 설정
     */
    val oreumRetrofitBuilder: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient) // 안전하지 않은 클라이언트 적용
        .build()

    /**
     * SSL 인증서를 우회하는 OkHttpClient 설정
     * @return SSL 인증서 검증을 우회한 OkHttpClient 인스턴스
     */
    fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(
                @SuppressLint("CustomX509TrustManager")
                object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {}

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        chain: Array<out X509Certificate>?,
                        authType: String?
                    ) {}

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory

            OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true } // 모든 호스트 인증서 허용
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}