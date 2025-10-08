package com.jeong.data.di

import com.jeong.data.local.permission.PermissionLocalDataSource
import com.jeong.data.local.permission.PreferencePermissionLocalDataSource
import com.jeong.data.repository.PermissionRepositoryImpl
import com.jeong.domain.repository.PermissionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PermissionDataModule {

    @Binds
    fun bindsPermissionRepository(
        impl: PermissionRepositoryImpl,
    ): PermissionRepository

    @Binds
    fun bindsPermissionLocalDataSource(
        impl: PreferencePermissionLocalDataSource,
    ): PermissionLocalDataSource
}
