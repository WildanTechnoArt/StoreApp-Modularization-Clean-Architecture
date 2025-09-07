package com.wildan.core.ui.helper

import android.view.View
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView

fun <T, VH : RecyclerView.ViewHolder> handleData(
    page: Int?,
    data: List<T>?,
    adapter: ListAdapter<T, VH>,
    recyclerView: RecyclerView?,
    emptyTextView: MaterialTextView?
) {
    val newData = data?.let {
        if (page != null && page > 1) {
            adapter.currentList + it
        } else {
            it
        }
    } ?: emptyList()

    adapter.submitList(newData) {
        if (page != null && page > 1 && data?.isNotEmpty() == true) {
            recyclerView?.smoothScrollToPosition(adapter.itemCount - data.size)
        }
    }

    val isEmpty = data.isNullOrEmpty()
    recyclerView?.visibility = if (isEmpty) View.GONE else View.VISIBLE
    emptyTextView?.visibility = if (isEmpty) View.VISIBLE else View.GONE
}