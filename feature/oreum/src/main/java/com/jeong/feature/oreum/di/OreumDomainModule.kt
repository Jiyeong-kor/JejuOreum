package com.jeong.feature.oreum.di

import com.jeong.feature.oreum.domain.DefaultOreumOverviewInteractor
import com.jeong.feature.oreum.domain.OreumOverviewInteractor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface OreumDomainModule {
    @Binds
    fun bindOreumOverviewInteractor(
        impl: DefaultOreumOverviewInteractor
    ): OreumOverviewInteractor
}
