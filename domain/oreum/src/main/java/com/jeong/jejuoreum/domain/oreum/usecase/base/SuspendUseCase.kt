package com.jeong.jejuoreum.domain.oreum.usecase.base

fun interface SuspendUseCase<in P, R> {
    suspend operator fun invoke(param: P): R
}
