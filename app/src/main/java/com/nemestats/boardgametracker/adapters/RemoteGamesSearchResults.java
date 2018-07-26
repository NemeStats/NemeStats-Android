package com.nemestats.boardgametracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.viewModels.UIViewModel.SearchResultBoardGame;

import java.util.List;

/**
 * Created by geomehedeniuc on 3/24/18.
 */

public class RemoteGamesSearchResults extends RecyclerView.Adapter<RemoteGamesSearchResults.ViewHolder> {

    private List<SearchResultBoardGame> mGamesList;
    public static final String NAME_FORMATTER = "%s (%s)";
    private OnItemClickListener mOnItemClickListener;

    public RemoteGamesSearchResults(@NonNull Context context, List<SearchResultBoardGame> gamesList, OnItemClickListener onItemClickListener) {
        mGamesList = gamesList;
        mOnItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RemoteGamesSearchResults.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_remote_game_search_results, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResultBoardGame searchResultBoardGame = mGamesList.get(position);
        if (searchResultBoardGame.getYear() != null) {
            holder.mGameName.setText(String.format(NAME_FORMATTER, searchResultBoardGame.getName(), searchResultBoardGame.getYear()));
        } else {
            holder.mGameName.setText(searchResultBoardGame.getName());
        }

        holder.mRootView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClicked(mGamesList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGamesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mGameName;
        private View mRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            mGameName = itemView.findViewById(R.id.game_name);
            mRootView = itemView.findViewById(R.id.root_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(SearchResultBoardGame searchResultBoardGame);
    }
}
