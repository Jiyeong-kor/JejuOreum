package com.jeong.jejuoreum.data.user.di

import com.jeong.jejuoreum.data.user.repository.UserAuthRepositoryImpl
import com.jeong.jejuoreum.data.user.repository.UserProfileRepositoryImpl
import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository
import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository
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
