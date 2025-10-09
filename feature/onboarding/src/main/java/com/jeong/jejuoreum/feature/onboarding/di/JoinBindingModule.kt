package com.jeong.jejuoreum.feature.onboarding.di

import com.jeong.jejuoreum.feature.onboarding.data.DomainAnonymousUserInitializer
import com.jeong.jejuoreum.feature.onboarding.data.DomainNicknameAvailabilityChecker
import com.jeong.jejuoreum.feature.onboarding.data.DomainNicknameSaver
import com.jeong.jejuoreum.feature.onboarding.data.DomainNicknameValidator
import com.jeong.jejuoreum.feature.onboarding.domain.AnonymousUserInitializer
import com.jeong.jejuoreum.feature.onboarding.domain.NicknameAvailabilityChecker
import com.jeong.jejuoreum.feature.onboarding.domain.NicknameSaver
import com.jeong.jejuoreum.feature.onboarding.domain.NicknameValidator
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
