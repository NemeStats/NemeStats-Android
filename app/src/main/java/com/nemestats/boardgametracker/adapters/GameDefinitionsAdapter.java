package com.nemestats.boardgametracker.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GameDefinitionViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by mehegeo on 10/8/17.
 */

public class GameDefinitionsAdapter extends RecyclerView.Adapter<GameDefinitionsAdapter.GameDefinitionViewHolder> {

    private List<GameDefinitionViewModel> mGameDefinitionViewModelList;
    private Picasso mPicassoInstance;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public GameDefinitionsAdapter(Context context, List<GameDefinitionViewModel> gameDefinitionViewModelList, OnItemClickListener onItemClickListener) {
        mContext = context;
        mGameDefinitionViewModelList = gameDefinitionViewModelList;
        mOnItemClickListener = onItemClickListener;
        mPicassoInstance = Picasso.with(mContext);
    }

    @Override
    public GameDefinitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new GameDefinitionViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_definition, parent, false));
    }

    @Override
    public void onBindViewHolder(GameDefinitionViewHolder holder, int position) {
        if (mGameDefinitionViewModelList.get(position) != null) {
            holder.mTxtGameDefinitionName.setText(mGameDefinitionViewModelList.get(position).getGameDefinition().getGameDefinitionName());
            if (mGameDefinitionViewModelList.get(position).getNumberOfPlayedGames() == 1) {
                holder.mTxtNumberOfPlayedGames.setText(String.format(mContext.getString(R.string.txt_single_game_played), mGameDefinitionViewModelList.get(position).getNumberOfPlayedGames()));
            } else {
                holder.mTxtNumberOfPlayedGames.setText(String.format(mContext.getString(R.string.txt_multiple_games_played), mGameDefinitionViewModelList.get(position).getNumberOfPlayedGames()));
            }

            if (mGameDefinitionViewModelList.get(position).isSelected()) {
                holder.mRootView.setBackgroundColor(mContext.getResources().getColor(R.color.bg_selected_game_definition));
                holder.mGameSelectedView.setVisibility(View.VISIBLE);
            } else {
                holder.mRootView.setBackgroundColor(0);
                holder.mGameSelectedView.setVisibility(View.GONE);
            }
            if (mGameDefinitionViewModelList.get(position).getGameDefinition().getThumbnailLocalPath() != null) {
                mPicassoInstance
                        .load(new File(mGameDefinitionViewModelList.get(position).getGameDefinition().getThumbnailLocalPath()))
                        .transform(new PicassoRoundedCornerTransformation())
                        .placeholder(R.drawable.placeholder_image)
                        .into(holder.mImgThumbnailGameDefinition);
            } else {
                mPicassoInstance
                        .load(R.drawable.placeholder_image)
                        .transform(new PicassoRoundedCornerTransformation())
                        .into(holder.mImgThumbnailGameDefinition);
            }
            holder.mRootView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(mGameDefinitionViewModelList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mGameDefinitionViewModelList.size();
    }

    public static class GameDefinitionViewHolder extends RecyclerView.ViewHolder {

        TextView mTxtGameDefinitionName;
        TextView mTxtNumberOfPlayedGames;
        ImageView mImgThumbnailGameDefinition;
        View mGameSelectedView;
        View mRootView;

        public GameDefinitionViewHolder(View itemView) {
            super(itemView);
            mTxtGameDefinitionName = itemView.findViewById(R.id.txt_game_definition_name);
            mTxtNumberOfPlayedGames = itemView.findViewById(R.id.txt_number_of_played_games);
            mImgThumbnailGameDefinition = itemView.findViewById(R.id.img_thumbnail_game_definition);
            mRootView = itemView.findViewById(R.id.root_view);
            mGameSelectedView = itemView.findViewById(R.id.game_definition_selected);
        }

        public TextView getTxtGameDefinitionName() {
            return mTxtGameDefinitionName;
        }

        public TextView getTxtNumberOfPlayedGames() {
            return mTxtNumberOfPlayedGames;
        }

        public ImageView getImgThumbnailGameDefinition() {
            return mImgThumbnailGameDefinition;
        }

        public View getRootView() {
            return mRootView;
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(GameDefinitionViewModel gameDefinitionViewModel);
    }
}
