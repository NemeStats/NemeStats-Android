package com.nemestats.boardgametracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GamingGroupViewModel;

import java.util.List;

/**
 * Created by geomehedeniuc on 5/1/18.
 */

public class GamingGroupsAdapter extends RecyclerView.Adapter<GamingGroupsAdapter.ViewHolder> {

    private List<GamingGroupViewModel> mGamingGroupViewModelList;
    private OnGamingGroupEventsListener mOnGamingGroupEventsListener;
    private Context mContext;

    public GamingGroupsAdapter(Context context, List<GamingGroupViewModel> gamingGroupViewModelList, OnGamingGroupEventsListener onGamingGroupEventsListener) {
        mContext = context;
        mGamingGroupViewModelList = gamingGroupViewModelList;
        mOnGamingGroupEventsListener = onGamingGroupEventsListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GamingGroupsAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gaming_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        GamingGroupViewModel gamingGroupViewModel = mGamingGroupViewModelList.get(holder.getAdapterPosition());
        if (gamingGroupViewModel != null) {
            holder.mTxtGamingGroupName.setText(gamingGroupViewModel.getGamingGroup().getGroupName());

            if (gamingGroupViewModel.getGamingGroup().getPublicDescription() != null && !gamingGroupViewModel.getGamingGroup().getPublicDescription().isEmpty()) {
                holder.mTxtGamingGroupDescription.setVisibility(View.VISIBLE);
                holder.mTxtGamingGroupDescription.setText(gamingGroupViewModel.getGamingGroup().getPublicDescription());
            } else {
                holder.mTxtGamingGroupDescription.setVisibility(View.GONE);
            }

            String result = "";
            if (gamingGroupViewModel.getNumberOfPlayedGamesInGamingGroup() == 1) {
                result += String.format(mContext.getResources().getString(R.string.txt_single_played_game), gamingGroupViewModel.getNumberOfPlayedGamesInGamingGroup());
            } else {
                result += String.format(mContext.getResources().getString(R.string.txt_multiple_played_games), gamingGroupViewModel.getNumberOfPlayedGamesInGamingGroup());
            }
            holder.mTxtPlaysNumber.setText(result);

            result = "";
            if (gamingGroupViewModel.getNumberOfGameDefinitionsInGamingGroup() == 1) {
                result += String.format(mContext.getResources().getString(R.string.txt_single_game), gamingGroupViewModel.getNumberOfGameDefinitionsInGamingGroup());
            } else {
                result += String.format(mContext.getResources().getString(R.string.txt_multiple_games), gamingGroupViewModel.getNumberOfGameDefinitionsInGamingGroup());
            }

            holder.mTxtGameDefinitionsNumber.setText(result);
            holder.mTxtPlayersNames.setText(gamingGroupViewModel.getPlayersNames());

            holder.mBtnMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.inflate(R.menu.menu_gaming_group);

                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_edit) {
                        if (mOnGamingGroupEventsListener != null) {
                            mOnGamingGroupEventsListener.onGamingGroupMenuEdit(mGamingGroupViewModelList.get(holder.getAdapterPosition()));
                        }
                    } else if (item.getItemId() == R.id.action_delete) {
                        if (mOnGamingGroupEventsListener != null) {
                            mOnGamingGroupEventsListener.onGamingGroupMenuDelete(mGamingGroupViewModelList.get(holder.getAdapterPosition()));
                        }
                    } else if (item.getItemId() == R.id.action_view_on_nemestats_dot_com) {
                        if (mOnGamingGroupEventsListener != null) {
                            mOnGamingGroupEventsListener.onGamingGroupMenuViewOnNemestats(mGamingGroupViewModelList.get(holder.getAdapterPosition()));
                        }
                    }
                    return true;
                });

                popupMenu.show();
            });

            holder.mRootView.setOnClickListener(v -> {
                if (mOnGamingGroupEventsListener != null) {
                    mOnGamingGroupEventsListener.onGamingGroupClicked(mGamingGroupViewModelList.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGamingGroupViewModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtGamingGroupName;
        TextView mTxtGamingGroupDescription;
        TextView mTxtPlaysNumber;
        TextView mTxtGameDefinitionsNumber;
        TextView mTxtPlayersNames;
        View mBtnMenu;
        View mRootView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTxtGamingGroupName = itemView.findViewById(R.id.txt_gaming_group_name);
            mTxtGamingGroupDescription = itemView.findViewById(R.id.txt_gaming_group_description);
            mTxtPlaysNumber = itemView.findViewById(R.id.txt_number_plays);
            mBtnMenu = itemView.findViewById(R.id.ic_menu);
            mTxtGameDefinitionsNumber = itemView.findViewById(R.id.txt_number_game_definitions);
            mTxtPlayersNames = itemView.findViewById(R.id.txt_players_names);
            mRootView = itemView.findViewById(R.id.root_view);
        }
    }

    public interface OnGamingGroupEventsListener {
        void onGamingGroupClicked(GamingGroupViewModel gamingGroupViewModel);

        void onGamingGroupMenuEdit(GamingGroupViewModel gamingGroupViewModel);

        void onGamingGroupMenuDelete(GamingGroupViewModel gamingGroupViewModel);

        void onGamingGroupMenuViewOnNemestats(GamingGroupViewModel gamingGroupViewModel);
    }
}
