package com.jeong.jejuoreum.core.common.usecase

fun interface SuspendUseCase<in P, R> {
    suspend operator fun invoke(params: P): R
}
