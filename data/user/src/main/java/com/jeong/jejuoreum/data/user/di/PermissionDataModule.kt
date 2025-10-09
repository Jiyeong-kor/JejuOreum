package com.jeong.jejuoreum.data.di

import com.jeong.jejuoreum.data.local.permission.PermissionLocalDataSource
import com.jeong.jejuoreum.data.local.permission.PreferencePermissionLocalDataSource
import com.jeong.jejuoreum.data.repository.PermissionRepositoryImpl
import com.jeong.jejuoreum.domain.repository.PermissionRepository
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
