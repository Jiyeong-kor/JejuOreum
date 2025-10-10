package com.jeong.jejuoreum.core.common.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

typealias ResultResource<T> = Result<Resource<T>>

fun <T> Resource<T>.asResultResource(): ResultResource<T> = Result.success(this)

fun <T> Result<T>.asResource(): Resource<T> = fold(
    onSuccess = { Resource.Success(it) },
    onFailure = { throwable -> Resource.Error(throwable) }
)

fun <T> Result<T>.asResultResource(): ResultResource<T> = Result.success(asResource())

fun <T> ResultResource<T>.resourceOrThrow(): Resource<T> = getOrElse { throwable ->
    throw throwable
}

fun <T> ResultResource<T>.dataOrNull(): T? = resourceOrThrow().dataOrNull

fun <T> ResultResource<T>.errorOrNull(): Throwable? =
    resourceOrThrow().errorOrNull

fun <T> Flow<Resource<T>>.asResultResourceFlow(): Flow<ResultResource<T>> =
    map { resource -> resource.asResultResource() }

fun <T> Flow<Result<T>>.asResultResourceFlow(): Flow<ResultResource<T>> =
    map { result -> result.asResultResource() }
