package com.jeong.jejuoreum.core.common.coroutines

import kotlinx.coroutines.CoroutineDispatcher

class DefaultCoroutineDispatcherProvider(
    override val io: CoroutineDispatcher,
    override val computation: CoroutineDispatcher,
    override val main: CoroutineDispatcher,
) : CoroutineDispatcherProvider
