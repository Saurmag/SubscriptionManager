package com.example.subscriptionmanager.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SubscriptionMargin : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildLayoutPosition(view) % 2 == 0) {
            outRect.top = 50
            outRect.left = 20
            outRect.right = 10
        } else {
            outRect.top = 50
            outRect.right = 20
            outRect.left = 10
        }
    }

}