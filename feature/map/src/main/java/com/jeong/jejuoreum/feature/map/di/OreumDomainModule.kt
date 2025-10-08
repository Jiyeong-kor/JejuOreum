package com.jeong.feature.oreum.di

import com.jeong.feature.oreum.domain.DefaultOreumDetailInteractor
import com.jeong.feature.oreum.domain.DefaultOreumOverviewInteractor
import com.jeong.feature.oreum.domain.OreumDetailInteractor
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

    @Binds
    fun bindOreumDetailInteractor(
        impl: DefaultOreumDetailInteractor
    ): OreumDetailInteractor
}
