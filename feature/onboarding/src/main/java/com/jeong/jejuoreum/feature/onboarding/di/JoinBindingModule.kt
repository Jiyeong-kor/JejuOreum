package com.jeong.jejuoreum.feature.join.di

import com.jeong.jejuoreum.feature.join.data.DomainAnonymousUserInitializer
import com.jeong.jejuoreum.feature.join.data.DomainNicknameAvailabilityChecker
import com.jeong.jejuoreum.feature.join.data.DomainNicknameSaver
import com.jeong.jejuoreum.feature.join.data.DomainNicknameValidator
import com.jeong.jejuoreum.feature.join.domain.AnonymousUserInitializer
import com.jeong.jejuoreum.feature.join.domain.NicknameAvailabilityChecker
import com.jeong.jejuoreum.feature.join.domain.NicknameSaver
import com.jeong.jejuoreum.feature.join.domain.NicknameValidator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class JoinBindingModule {

    @Binds
    abstract fun bindNicknameValidator(
        implementation: DomainNicknameValidator,
    ): NicknameValidator

    @Binds
    abstract fun bindNicknameAvailabilityChecker(
        implementation: DomainNicknameAvailabilityChecker,
    ): NicknameAvailabilityChecker

    @Binds
    abstract fun bindNicknameSaver(
        implementation: DomainNicknameSaver,
    ): NicknameSaver

    @Binds
    abstract fun bindAnonymousUserInitializer(
        implementation: DomainAnonymousUserInitializer,
    ): AnonymousUserInitializer
}
