package com.jeong.jjoreum.di

import com.jeong.jjoreum.repository.OreumRepository
import com.jeong.jjoreum.repository.OreumRepositoryImpl
import com.jeong.jjoreum.repository.ReviewRepository
import com.jeong.jjoreum.repository.ReviewRepositoryImpl
import com.jeong.jjoreum.repository.StampRepository
import com.jeong.jjoreum.repository.StampRepositoryImpl
import com.jeong.jjoreum.repository.UserInteractionRepository
import com.jeong.jjoreum.repository.UserInteractionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindOreumRepository(impl: OreumRepositoryImpl): OreumRepository

    @Singleton
    @Binds
    abstract fun bindReviewRepository(impl: ReviewRepositoryImpl): ReviewRepository

    @Singleton
    @Binds
    abstract fun bindStampRepository(impl: StampRepositoryImpl): StampRepository

    @Singleton
    @Binds
    abstract fun bindUserInteractionRepository(
        impl: UserInteractionRepositoryImpl
    ): UserInteractionRepository
}
