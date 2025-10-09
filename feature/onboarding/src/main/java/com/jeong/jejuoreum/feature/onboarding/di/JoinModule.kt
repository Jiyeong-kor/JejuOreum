package com.jeong.jejuoreum.feature.join.di

import com.jeong.jejuoreum.domain.user.repository.UserAuthRepository
import com.jeong.jejuoreum.domain.user.repository.UserProfileRepository
import com.jeong.jejuoreum.domain.user.usecase.CheckNicknameAvailabilityUseCase
import com.jeong.jejuoreum.domain.user.usecase.EnsureAnonymousUserUseCase
import com.jeong.jejuoreum.domain.user.usecase.SaveNicknameUseCase
import com.jeong.jejuoreum.domain.user.usecase.ValidateNicknameUseCase
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
    ): CheckNicknameAvailabilityUseCase =
        CheckNicknameAvailabilityUseCase(userProfileRepository)

    @Provides
    fun provideSaveNicknameUseCase(
        userProfileRepository: UserProfileRepository,
    ): SaveNicknameUseCase = SaveNicknameUseCase(userProfileRepository)

    @Provides
    fun provideEnsureAnonymousUserUseCase(
        userAuthRepository: UserAuthRepository,
    ): EnsureAnonymousUserUseCase = EnsureAnonymousUserUseCase(userAuthRepository)
}
