package com.jeong.jejuoreum.feature.map.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jejuoreum.data.oreum.repository.OreumRepositoryImpl
import com.jeong.jejuoreum.data.review.repository.ReviewRepositoryImpl
import com.jeong.jejuoreum.data.oreum.repository.StampRepositoryImpl
import com.jeong.jejuoreum.data.user.repository.UserInteractionRepositoryImpl
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import com.jeong.jejuoreum.domain.oreum.repository.StampRepository
import com.jeong.jejuoreum.domain.user.repository.UserInteractionRepository
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
