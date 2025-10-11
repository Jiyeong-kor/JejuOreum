package com.jeong.jejuoreum.feature.profile.presentation.favorite

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class MyFavoritePresentationModule {

    @Binds
    abstract fun bindMyFavoriteErrorMessageProvider(
        impl: AndroidMyFavoriteErrorMessageProvider,
    ): MyFavoriteErrorMessageProvider
}
