package com.nemestats.boardgametracker.customViews;

/**
 * Created by geomehedeniuc on 3/17/18.
 */


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nemestats.boardgametracker.R;

public class FlowLayout extends ViewGroup {
    private int mHorizontalSpacing;
    private int mVerticalSpacing;
    private OnSizeChangedListener mOnSizeChangedListener;

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mVerticalSpacing = (int) getResources().getDimension(R.dimen.dimen_vertical_spacing_selected_players);
        mHorizontalSpacing = (int) getResources().getDimension(R.dimen.dimen_vertical_spacing_selected_players);
    }

    public void setOnSizeChangedListener(OnSizeChangedListener onSizeChangedListener) {
        mOnSizeChangedListener = onSizeChangedListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        boolean growHeight = widthMode != MeasureSpec.UNSPECIFIED;

        int width = 0;
        int height = getPaddingTop();

        int currentWidth = getPaddingLeft();
        int currentHeight = 0;

        boolean newLine = false;
        int spacing = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            spacing = mHorizontalSpacing;

            if (growHeight && (currentWidth + child.getMeasuredWidth() > widthSize)) {
                height += currentHeight + mVerticalSpacing;
                currentHeight = 0;
                width = Math.max(width, currentWidth - spacing);
                currentWidth = 0;
                newLine = true;
            } else {
                newLine = false;
            }

            layoutParams.x = currentWidth;
            layoutParams.y = height;

            currentWidth += child.getMeasuredWidth() + spacing;
            currentHeight = Math.max(currentHeight, child.getMeasuredHeight());
        }

        height += currentHeight;

        if (!newLine) {
            width = Math.max(width, currentWidth - spacing);
        } else {
            width = 0;
        }

        width += getPaddingRight();
        height += getPaddingBottom();

        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            child.layout(lp.x, lp.y, lp.x + child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mOnSizeChangedListener != null) {
            mOnSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }

    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        int x;
        int y;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(int w, int h, int oldw, int oldh);
    }

}
