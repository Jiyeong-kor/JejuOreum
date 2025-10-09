package com.jeong.jejuoreum.feature.oreum.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jeong.jejuoreum.data.repository.OreumRepositoryImpl
import com.jeong.jejuoreum.data.repository.ReviewRepositoryImpl
import com.jeong.jejuoreum.data.repository.StampRepositoryImpl
import com.jeong.jejuoreum.data.repository.UserInteractionRepositoryImpl
import com.jeong.jejuoreum.domain.repository.OreumRepository
import com.jeong.jejuoreum.domain.repository.ReviewRepository
import com.jeong.jejuoreum.domain.repository.StampRepository
import com.jeong.jejuoreum.domain.repository.UserInteractionRepository
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
