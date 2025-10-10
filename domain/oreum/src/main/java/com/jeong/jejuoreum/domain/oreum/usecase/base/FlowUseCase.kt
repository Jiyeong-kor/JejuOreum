package com.jeong.jejuoreum.domain.oreum.usecase.base

fun interface FlowUseCase<in P, R> {
    suspend operator fun invoke(param: P): R
}
