package com.jeong.feature.join.data

import com.jeong.domain.usecase.SaveNicknameUseCase
import com.jeong.feature.join.domain.NicknameSaver
import javax.inject.Inject

class DomainNicknameSaver @Inject constructor(
    private val saveNicknameUseCase: SaveNicknameUseCase,
) : NicknameSaver {
    override suspend fun save(nickname: String) = saveNicknameUseCase(nickname)
}
