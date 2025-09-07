package com.wildan.core.extension

import java.text.NumberFormat
import java.util.Locale

fun Double?.toRupiah(): String {
    return NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        .format(this ?: 0.0).replace(",00", "")
}