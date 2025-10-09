package com.jeong.jejuoreum.feature.map.di

import com.jeong.jejuoreum.feature.detail.domain.DefaultOreumDetailInteractor
import com.jeong.jejuoreum.feature.detail.domain.OreumDetailInteractor
import com.jeong.jejuoreum.feature.map.domain.DefaultOreumOverviewInteractor
import com.jeong.jejuoreum.feature.map.domain.OreumOverviewInteractor
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
