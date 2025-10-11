package com.jeong.jejuoreum.domain.oreum.usecase

import com.jeong.jejuoreum.core.common.error.toResourceError
import com.jeong.jejuoreum.core.common.result.Resource
import com.jeong.jejuoreum.domain.oreum.model.Oreum
import com.jeong.jejuoreum.domain.oreum.repository.OreumRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ObserveOreumsUseCase @Inject constructor(
    private val repository: OreumRepository,
) {

    operator fun invoke(): Flow<Resource<List<Oreum>>> =
        repository.observeOreums()
            .map<List<Oreum>, Resource<List<Oreum>>> { oreums ->
                Resource.Success(oreums)
            }
            .onStart { emit(Resource.Loading) }
            .catch { throwable -> emit(Resource.Error(throwable.toResourceError())) }
}
