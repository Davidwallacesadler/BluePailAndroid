package com.davidsadler.bluepail.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecorator(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            top = spaceHeight
            if (parent.getChildAdapterPosition(view).rem(2) == 0) {
                left = spaceHeight
                right = spaceHeight.div(2)
            } else {
                right = spaceHeight
                left = spaceHeight.div(2)
            }
        }
    }
}