package com.jeong.jejuoreum.feature.join.data

import com.jeong.jejuoreum.domain.user.usecase.SaveNicknameUseCase
import com.jeong.jejuoreum.feature.join.domain.NicknameSaver
import javax.inject.Inject

class DomainNicknameSaver @Inject constructor(
    private val saveNicknameUseCase: SaveNicknameUseCase,
) : NicknameSaver {
    override suspend fun save(nickname: String) = saveNicknameUseCase(nickname)
}
