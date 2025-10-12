package com.jeong.jejuoreum.data.oreum.remote.source

import com.jeong.jejuoreum.core.common.coroutines.CoroutineDispatcherProvider
import com.jeong.jejuoreum.core.common.error.DomainError
import com.jeong.jejuoreum.data.oreum.remote.api.OreumRetrofitInterface
import com.jeong.jejuoreum.domain.oreum.entity.ResultSummary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Singleton
class NetworkOreumRemoteDataSource @Inject constructor(
    private val api: OreumRetrofitInterface,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) : OreumRemoteDataSource {

    override suspend fun fetchOreums(): List<ResultSummary> =
        withContext(dispatcherProvider.io) {
            runCatching {
                val response = api.getOreumList()
                if (!response.isSuccessful) throw HttpException(response)
                response.body()?.resultSummary?.map(ResultSummary::copy) ?: emptyList()
            }.getOrElse { error ->
                throw DomainError.Unknown(error)
            }
        }

    override suspend fun fetchOreum(id: String): ResultSummary? =
        fetchOreums().firstOrNull { it.idx.toString() == id }
}
