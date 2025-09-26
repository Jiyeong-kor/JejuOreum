package com.jeong.jjoreum.di

import com.jeong.data.repository.UserAuthRepositoryImpl
import com.jeong.data.repository.UserProfileRepositoryImpl
import com.jeong.domain.repository.UserAuthRepository
import com.jeong.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl,
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindUserAuthRepository(
        impl: UserAuthRepositoryImpl,
    ): UserAuthRepository
}
