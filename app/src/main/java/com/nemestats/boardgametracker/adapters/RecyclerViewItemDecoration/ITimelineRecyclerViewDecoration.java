package com.nemestats.boardgametracker.adapters.RecyclerViewItemDecoration;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Geo on 2/27/17.
 */

public interface ITimelineRecyclerViewDecoration<T extends RecyclerView.ViewHolder> {

    /**
     * Returns the mHeader id for the item at the given position.
     *
     * @param position the item position
     * @return the mHeader id
     */
    long getHeaderId(int position);

    /**
     * Creates a new mHeader ViewHolder.
     *
     * @param parent the mHeader's view parent
     * @return a view holder for the created view
     */
    T onCreateHeaderViewHolder(ViewGroup parent);

    /**
     * Updates the mHeader view to reflect the mHeader data for the given position
     *
     * @param viewholder the mHeader view holder
     * @param position   the mHeader's item position
     */
    void onBindHeaderViewHolder(T viewholder, int position);
}