package com.lumiwallet.lumi_core.presentation


import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderItemDecoration(
    private val mRecyclerView: RecyclerView,
    private val mListener: StickyHeaderInterface
) : RecyclerView.ItemDecoration() {

    private val mHeaderContainer = FrameLayout(mRecyclerView.context)
    private var mStickyHeaderHeight: Int = 0
    private var mCurrentHeader: View? = null
    private var mCurrentHeaderPosition = 0
    private var mPrevPosition = -1
    private var mPrevMarginTop = -1

    init {
        val layout = ConstraintLayout(mRecyclerView.context)
        val params = mRecyclerView.layoutParams
        val parent = mRecyclerView.parent as ViewGroup
        val index = parent.indexOfChild(mRecyclerView)
        parent.addView(layout, index, params)
        parent.removeView(mRecyclerView)
        layout.addView(
            mRecyclerView, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )
        layout.addView(
            mHeaderContainer, ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            mHeaderContainer.visibility = View.INVISIBLE
            return
        }

        if (mHeaderContainer.visibility == View.INVISIBLE) {
            mHeaderContainer.visibility = View.VISIBLE
        }

        if ((mRecyclerView.layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition() == 0
        ) {
            mHeaderContainer.visibility = View.INVISIBLE
            return
        }

        val currentHeader = getHeaderViewForItem(topChildPosition, parent)
        fixLayoutSize(parent, currentHeader)
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint) ?: return

        val nextPosition = parent.getChildAdapterPosition(childInContact)
        if (mListener.isHeader(nextPosition)) {
            moveHeader(currentHeader, childInContact, topChildPosition, nextPosition)
            return
        }

        drawHeader(currentHeader, topChildPosition)
    }

    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View {
        val headerPosition = mListener.getHeaderPositionForItem(itemPosition)
        val layoutResId = mListener.getHeaderLayout(headerPosition)
        val header = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        mListener.bindHeaderData(header, headerPosition)
        return header
    }

    private fun drawHeader(header: View, position: Int) {
        if (mPrevPosition != position) {
            mPrevPosition = position
            mHeaderContainer.layoutParams.height = mStickyHeaderHeight
            mHeaderContainer.visibility = View.VISIBLE
            setCurrentHeader(header, position)
        }
    }

    private fun moveHeader(currentHead: View, nextHead: View, currentPos: Int, nextPos: Int) {
        val marginTop = nextHead.top - currentHead.height
        if (mPrevMarginTop != marginTop || mPrevPosition != currentPos) {
            mPrevMarginTop = marginTop
            mPrevPosition = currentPos
            if (mCurrentHeaderPosition == nextPos && currentPos != nextPos) {
                setCurrentHeader(currentHead, currentPos)
            }

            val params = mCurrentHeader?.layoutParams as? ViewGroup.MarginLayoutParams ?: return
            params.setMargins(0, marginTop, 0, 0)
            mCurrentHeader?.layoutParams = params

            mHeaderContainer.layoutParams.height = mStickyHeaderHeight + marginTop
        }
    }

    private fun setCurrentHeader(header: View, position: Int) {
        mCurrentHeader = header
        mCurrentHeaderPosition = position
        mHeaderContainer.removeAllViews()
        mHeaderContainer.addView(mCurrentHeader)
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? =
        (0 until parent.childCount)
            .map { parent.getChildAt(it) }
            .firstOrNull { it.bottom > contactPoint && it.top <= contactPoint }

    private fun fixLayoutSize(parent: ViewGroup, view: View) {

        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)

        mStickyHeaderHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, mStickyHeaderHeight)
    }

    interface StickyHeaderInterface {

        fun getHeaderPositionForItem(itemPosition: Int): Int

        fun getHeaderLayout(headerPosition: Int): Int

        fun bindHeaderData(header: View, headerPosition: Int)

        fun isHeader(itemPosition: Int): Boolean
    }
}