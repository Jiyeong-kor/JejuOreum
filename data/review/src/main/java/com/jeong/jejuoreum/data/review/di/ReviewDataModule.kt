package com.jeong.jejuoreum.data.review.di

import com.jeong.jejuoreum.data.review.repository.ReviewRepositoryImpl
import com.jeong.jejuoreum.domain.review.repository.ReviewRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ReviewDataModule {

    @Binds
    @Singleton
    fun bindsReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository
}
