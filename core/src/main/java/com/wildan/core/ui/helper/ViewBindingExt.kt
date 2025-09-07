package com.wildan.core.ui.helper

import android.content.Context
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ViewBindingExt {

    inline fun <T : ViewBinding> ComponentActivity.viewBinding(
        crossinline bindingInflater: (LayoutInflater) -> T
    ) = lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }

    inline fun <T : ViewBinding> Context.createAlertDialog(
        title: String?,
        crossinline bindingInflater: (LayoutInflater) -> T,
        dialogBuilder: (T, AlertDialog) -> Unit
    ): AlertDialog {
        val binding = bindingInflater(LayoutInflater.from(this))
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setView(binding.root)
            .setCancelable(false)
            .create()

        dialogBuilder(binding, dialog)
        return dialog
    }
}