package com.wildan.core.extension

import android.text.TextUtils
import android.util.Patterns

fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhoneNumber(): Boolean {
    return this.matches(Regex("^(\\+\\d{1,3}|0)\\d{9,15}$"))
}

fun String?.isNotEmpty(): Boolean {
    return !TextUtils.isEmpty(this)
}