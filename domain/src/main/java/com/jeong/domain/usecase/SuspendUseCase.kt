package com.jeong.domain.usecase

fun interface SuspendUseCase<in P, R> {
    suspend operator fun invoke(params: P): R
}
