package com.nemestats.boardgametracker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by geomehedeniuc on 3/17/18.
 */

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ItemViewHolder> {

    private List<PlayerViewModel> mPlayerViewModelList;
    private OnPlayerClickListener mOnPlayerClickListener;
    private Context mContext;

    public PlayersAdapter(Context context, List<PlayerViewModel> playerViewModelList, OnPlayerClickListener onPlayerClickListener) {
        mContext = context;
        mPlayerViewModelList = playerViewModelList;
        mOnPlayerClickListener = onPlayerClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayersAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.mTxtPlayerName.setText(mPlayerViewModelList.get(position).getPlayer().getPlayerName());
        holder.mTxtBadge.setText(mPlayerViewModelList.get(position).getPlayer().getPlayerName().substring(0, 1));
        if (mPlayerViewModelList.get(position).isSelected()) {
            holder.mPlayerSelected.setVisibility(View.VISIBLE);
        } else {
            holder.mPlayerSelected.setVisibility(View.GONE);
        }
        String playerRemoteAvatar = mPlayerViewModelList.get(position).getPlayer().getRemoteAvatarUrl();

        if (playerRemoteAvatar != null && !playerRemoteAvatar.isEmpty()) {
            Log.e("Avatar", "onBindViewHolder: Show avatar");
            holder.mPlayerAvatar.setVisibility(View.VISIBLE);
            Picasso.with(mContext)
                    .load(playerRemoteAvatar)
                    .transform(new PicassoRoundedCornerTransformation(1f))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.mPlayerAvatar);
        } else {
            holder.mPlayerAvatar.setVisibility(View.GONE);
        }

        holder.mRootView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition >= 0 && adapterPosition < mPlayerViewModelList.size()) {
                if (mOnPlayerClickListener != null) {
                    mOnPlayerClickListener.onPlayerClicked(mPlayerViewModelList.get(adapterPosition), adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayerViewModelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        View mRootView;
        TextView mTxtPlayerName;
        TextView mTxtBadge;
        ImageView mPlayerSelected;
        ImageView mPlayerAvatar;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView.findViewById(R.id.root_view);
            mTxtPlayerName = itemView.findViewById(R.id.txt_player_name);
            mTxtBadge = itemView.findViewById(R.id.txt_badge);
            mPlayerSelected = itemView.findViewById(R.id.player_selected);
            mPlayerAvatar = itemView.findViewById(R.id.player_avatar);
        }
    }

    public interface OnPlayerClickListener {
        void onPlayerClicked(PlayerViewModel playerViewModel, int position);
    }
}
