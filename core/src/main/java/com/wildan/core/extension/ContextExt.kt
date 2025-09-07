package com.wildan.core.extension

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showAlertDialog(message: String, action: (DialogInterface) -> Unit) {
    val alert = MaterialAlertDialogBuilder(this).apply {
        setTitle("Confirmation")
        setMessage(message)
        setPositiveButton("Yes") { dialog, _ ->
            action(dialog)
        }
        setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
    }
    alert.create()
    alert.show()
}