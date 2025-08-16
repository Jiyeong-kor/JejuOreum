package com.jeong.jjoreum.di

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import coil3.ImageLoader
import coil3.asImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.jeong.jjoreum.BuildConfig
import com.jeong.jjoreum.R
import com.jeong.jjoreum.data.model.api.OreumRetrofitInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    private const val BASE_URL = BuildConfig.JEJU_OREUM_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager") object :
                X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                override fun checkClientTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                override fun checkServerTrusted(
                    chain: Array<out X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())

        return OkHttpClient.Builder()
            .sslSocketFactory(
                sslContext.socketFactory, trustAllCerts[0] as X509TrustManager
            )
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(
                20, TimeUnit.SECONDS
            ).readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).client(okHttpClient).build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): OreumRetrofitInterface {
        return retrofit.create(OreumRetrofitInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        val placeholder =
            AppCompatResources.getDrawable(context, R.drawable.placeholder_image)!!.asImage()
        val error =
            AppCompatResources.getDrawable(context, R.drawable.error_image)!!.asImage()

        return ImageLoader.Builder(context)
            .placeholder(placeholder)
            .error(error)
            .fallback(placeholder)
            .crossfade(true)
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient))
            }
            .build()
    }
}