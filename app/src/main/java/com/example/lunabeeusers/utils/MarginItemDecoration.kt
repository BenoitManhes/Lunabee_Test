package com.example.lunabeeusers.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom decorator for the Recyclerview
 */
class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

    /**
     * Setting space between Recycler View’s cell
     */
    override fun getItemOffsets(outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceHeight
            }
            bottom = spaceHeight
        }
    }
}
