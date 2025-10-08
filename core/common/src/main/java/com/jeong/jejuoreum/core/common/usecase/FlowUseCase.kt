package com.jeong.jejuoreum.core.common.usecase

import kotlinx.coroutines.flow.Flow

fun interface FlowUseCase<in P, R> {
    operator fun invoke(params: P): Flow<R>
}
