package com.jeong.jjoreum.di

import com.jeong.domain.repository.OreumRepository
import com.jeong.domain.repository.UserInteractionRepository
import com.jeong.domain.usecase.ToggleFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideToggleFavoriteUseCase(
        userInteractionRepository: UserInteractionRepository,
        oreumRepository: OreumRepository
    ): ToggleFavoriteUseCase = ToggleFavoriteUseCase(
        userInteractionRepository,
        oreumRepository
    )
}
