package com.wildan.core.error

sealed class AppError {
    data class Api(val code: Int, val message: String?) : AppError()
    data object NoConnection : AppError()
    data object Timeout : AppError()
    data class Unknown(val throwable: Throwable) : AppError()
}