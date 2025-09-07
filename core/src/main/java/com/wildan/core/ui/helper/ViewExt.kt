package com.wildan.core.ui.helper

import android.view.View
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LifecycleOwner
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun MaterialButton.initProgressButton(lifecycle: LifecycleOwner) {
    lifecycle.bindProgressButton(this)
    this.attachTextChangeAnimator()
}

fun MaterialButton.setLoading(isLoading: Boolean) {
    this.isClickable = !isLoading
    val title = this.text.toString()
    if (isLoading) {
        this.showProgress { progressColor = "#FFFFFF".toColorInt() }
    } else {
        this.hideProgress(title)
    }
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.setOnSingleClickListener(interval: Long = 600L, action: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        if (System.currentTimeMillis() - lastClickTime < interval) return@setOnClickListener
        lastClickTime = System.currentTimeMillis()
        action(it)
    }
}