package com.jeong.jjoreum.di

import com.jeong.domain.repository.UserAuthRepository
import com.jeong.domain.repository.UserProfileRepository
import com.jeong.domain.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.domain.usecase.EnsureAnonymousUserUseCase
import com.jeong.domain.usecase.SaveNicknameUseCase
import com.jeong.domain.usecase.ValidateNicknameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object JoinModule {

    @Provides
    fun provideValidateNicknameUseCase(): ValidateNicknameUseCase = ValidateNicknameUseCase()

    @Provides
    fun provideCheckNicknameAvailabilityUseCase(
        userProfileRepository: UserProfileRepository,
    ): CheckNicknameAvailabilityUseCase = CheckNicknameAvailabilityUseCase(userProfileRepository)

    @Provides
    fun provideSaveNicknameUseCase(
        userProfileRepository: UserProfileRepository,
    ): SaveNicknameUseCase = SaveNicknameUseCase(userProfileRepository)

    @Provides
    fun provideEnsureAnonymousUserUseCase(
        userAuthRepository: UserAuthRepository,
    ): EnsureAnonymousUserUseCase = EnsureAnonymousUserUseCase(userAuthRepository)
}
