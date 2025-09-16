package com.jeong.feature.oreum.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.data.repository.OreumRepositoryImpl
import com.jeong.data.repository.ReviewRepositoryImpl
import com.jeong.data.repository.StampRepositoryImpl
import com.jeong.data.repository.UserInteractionRepositoryImpl
import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.repository.ReviewRepository
import com.jeong.domain.repository.StampRepository
import com.jeong.domain.repository.UserInteractionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OreumRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindOreumRepository(impl: OreumRepositoryImpl): OreumRepository

    @Singleton
    @Binds
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Singleton
    @Binds
    abstract fun bindUserInteractionRepository(
        impl: UserInteractionRepositoryImpl
    ): UserInteractionRepository

    companion object {
        @Provides
        @Singleton
        fun provideStampRepository(
            @ApplicationContext context: Context,
            firestore: FirebaseFirestore,
            auth: FirebaseAuth,
        ): StampRepository = StampRepositoryImpl(context, firestore, auth)
    }
}
