package com.nemestats.boardgametracker.adapters.RecyclerViewItemDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.nemestats.boardgametracker.R;

import java.util.HashMap;
import java.util.Map;

public class TimelineRecyclerViewDecoration extends RecyclerView.ItemDecoration {
    public static final long NO_HEADER_ID = -1L;

    private Map<Long, RecyclerView.ViewHolder> mHeaderCache;

    private ITimelineRecyclerViewDecoration mAdapter;

    private boolean mRenderInline;
    private Context mContext;
    private int mItemDivider;
    private int mHeaderOffset;

    public TimelineRecyclerViewDecoration(ITimelineRecyclerViewDecoration adapter, boolean renderInline, Context context) {
        mAdapter = adapter;
        mHeaderCache = new HashMap<>();
        mRenderInline = renderInline;
        mContext = context;
        mItemDivider = (int) mContext.getResources().getDimension(R.dimen.item_decoration_height);
        mHeaderOffset = (int) mContext.getResources().getDimension(R.dimen.header_offset);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int headerHeight = mItemDivider;
        int bottom = 0;

        if (position != RecyclerView.NO_POSITION
                && hasHeader(position)
                && showHeaderAboveItem(position)) {

            View header = getHeader(parent, position).itemView;
            headerHeight = getHeaderHeightForLayout(header);
        }
        if (position == parent.getAdapter().getItemCount() - 1) {
            bottom = mItemDivider;
        }


        outRect.set(0, headerHeight, 0, bottom);
    }

    private boolean showHeaderAboveItem(int itemAdapterPosition) {
        return itemAdapterPosition == 0 || mAdapter.getHeaderId(itemAdapterPosition - 1) != mAdapter.getHeaderId(itemAdapterPosition);
    }

    /**
     * Clears the mHeader view cache. Headers will be recreated and
     * rebound on list scroll after this method has been called.
     */
    public void clearHeaderCache() {
        mHeaderCache.clear();
    }

    private boolean hasHeader(int position) {
        return mAdapter.getHeaderId(position) != NO_HEADER_ID;
    }

    private RecyclerView.ViewHolder getHeader(RecyclerView parent, int position) {
        final long key = mAdapter.getHeaderId(position);

        if (mHeaderCache.containsKey(key)) {
            return mHeaderCache.get(key);
        } else {
            final RecyclerView.ViewHolder holder = mAdapter.onCreateHeaderViewHolder(parent);
            final View header = holder.itemView;

            //noinspection unchecked
            mAdapter.onBindHeaderViewHolder(holder, position);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.UNSPECIFIED);

            int childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
                    parent.getPaddingLeft() + parent.getPaddingRight(), header.getLayoutParams().width);
            int childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
                    parent.getPaddingTop() + parent.getPaddingBottom(), header.getLayoutParams().height);

            header.measure(childWidth, childHeight);
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());

            mHeaderCache.put(key, holder);

            return holder;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        final int count = parent.getChildCount();
        long previousHeaderId = -1;

        for (int layoutPos = 0; layoutPos < count; layoutPos++) {
            final View child = parent.getChildAt(layoutPos);
            final int adapterPos = parent.getChildAdapterPosition(child);

            if (adapterPos != RecyclerView.NO_POSITION && hasHeader(adapterPos)) {
                long headerId = mAdapter.getHeaderId(adapterPos);

                if (headerId != previousHeaderId) {
                    previousHeaderId = headerId;
                    View header = getHeader(parent, adapterPos).itemView;
                    canvas.save();

                    final int left = child.getLeft();
                    final int top = getHeaderTop(parent, child, header, adapterPos, layoutPos);
                    canvas.translate(left, top - mHeaderOffset / 2);

                    header.setTranslationX(left);
                    header.setTranslationY(top);
                    header.draw(canvas);
                    canvas.restore();
                }
            }
        }
    }

    private int getHeaderTop(RecyclerView parent, View child, View header, int adapterPos, int layoutPos) {
        int headerHeight = getHeaderHeightForLayout(header);
        return ((int) child.getY()) - headerHeight;
    }

    private int getHeaderHeightForLayout(View header) {
        return mRenderInline ? 0 : header.getHeight() - mHeaderOffset;
    }
}
