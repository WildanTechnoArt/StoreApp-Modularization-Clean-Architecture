package com.wildan.core.error

import com.google.gson.Gson
import retrofit2.HttpException
import java.net.SocketTimeoutException

object ErrorMapper {
    fun map(error: Throwable, isConnection: Boolean): AppError {
        if(!isConnection) return AppError.NoConnection
        return when (error) {
            is HttpException -> {
                val message = try {
                    val errorBody = error.response()?.errorBody()?.string()
                    if (errorBody?.startsWith("{") == true) {
                        val response = Gson().fromJson(errorBody,
                            ApiErrorResponse::class.java
                        )
                        response?.message
                    } else {
                        errorBody ?: "Unknown error"
                    }
                } catch (e: Exception) {
                    e.localizedMessage
                }

                AppError.Api(error.code(), message)
            }

            is SocketTimeoutException -> AppError.Timeout
            else -> AppError.Unknown(error)
        }
    }
}