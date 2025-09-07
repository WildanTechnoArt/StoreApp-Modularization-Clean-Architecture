package com.wildan.core.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    fun isConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetwork != null
    }
}