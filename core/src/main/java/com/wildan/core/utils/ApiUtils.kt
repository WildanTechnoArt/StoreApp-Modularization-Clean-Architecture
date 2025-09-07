package com.wildan.core.utils

import androidx.fragment.app.FragmentActivity
import com.wildan.core.R
import com.wildan.core.error.AppError
import com.wildan.core.error.ErrorMapper
import com.wildan.core.extension.showToast
import com.wildan.core.utils.NetworkUtil.isConnected

fun FragmentActivity.handleErrorApi(error: Throwable) {
    when (val appError = ErrorMapper.map(error, isConnected(this))) {
        is AppError.Api -> {
            if (appError.code == 401) {
                showToast("Sorry your session has ended")
                finishAffinity()
            } else {
                val message = appError.message
                if (!message.isNullOrBlank() && message != "null") {
                    showToast(message)
                }
            }
        }

        is AppError.Timeout -> showToast("Connection Timeout!")
        is AppError.NoConnection -> showToast(getString(R.string.message_if_disconnect))
        is AppError.Unknown -> showToast(appError.throwable.localizedMessage ?: "Unknown Error")
    }
}