package com.nemestats.boardgametracker.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.adapters.RecyclerViewItemDecoration.ITimelineRecyclerViewDecoration;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.nemestats.boardgametracker.utils.DateUtils;
import com.nemestats.boardgametracker.utils.OrdinalIndicatorUtils;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayedGameHeaderData;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayedGameViewModel;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mehegeo on 10/12/17.
 */

public class PlayedGamesAdapter extends RecyclerView.Adapter<PlayedGamesAdapter.ItemViewHolder> implements ITimelineRecyclerViewDecoration<PlayedGamesAdapter.HeaderHolder> {

    private final Picasso mPicassoInstance;
    private Context mContext;
    private List<PlayedGameViewModel> mPlayedGameViewModels;
    private final HashMap<Integer, PlayedGameHeaderData> mHeadersPositions;
    private OnItemClickListener mOnItemClickListener;
    DateTimeFormatter mDateTimeFormatter;

    public PlayedGamesAdapter(Context context, List<PlayedGameViewModel> playedGameViewModels, HashMap<Integer, PlayedGameHeaderData> headersPositions, OnItemClickListener onItemClickListener) {
        mContext = context;
        mPlayedGameViewModels = playedGameViewModels;
        mHeadersPositions = headersPositions;
        mOnItemClickListener = onItemClickListener;
        mPicassoInstance = Picasso.with(mContext);
        mDateTimeFormatter = DateTimeFormat.forPattern("dd MMM yyyy");
    }

    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayedGamesAdapter.ItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_played_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (mPlayedGameViewModels.get(holder.getAdapterPosition()).getGameDefinition() == null || !mPlayedGameViewModels.get(holder.getAdapterPosition()).getGameDefinition().isActive()) {
            holder.mTxtGameDefinitionName.setText(String.format(mContext.getString(R.string.inactive), mPlayedGameViewModels.get(position).getPlayedGame().getGameDefinitionName()));
        } else {
            holder.mTxtGameDefinitionName.setText(mPlayedGameViewModels.get(position).getPlayedGame().getGameDefinitionName());
        }

        holder.mContainerGameRankings.removeAllViews();

        List<PlayerGameResults> playerGameResultsList = mPlayedGameViewModels.get(position).getPlayedGame().getPlayerGameResultsList();

        int listSize = playerGameResultsList.size();
        int i = 0;
        while (i < listSize) {

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gameResultRank = layoutInflater.inflate(R.layout.game_result_rank, null);

            if (i == 0) {
                View connectingLineTop = gameResultRank.findViewById(R.id.connecting_line_top);
                connectingLineTop.setVisibility(View.GONE);
            }

            TextView txtRank = gameResultRank.findViewById(R.id.txt_badge_rank_count);

            TextView txtPlayerName = gameResultRank.findViewById(R.id.txt_player_name);

            AppCompatImageView imageTeamWinLoss = gameResultRank.findViewById(R.id.img_team_win_loss);

            int currentGameRank = playerGameResultsList.get(i).getGameRank();

            if (mPlayedGameViewModels.get(position).getPlayedGame().getGameResultType().equals(PlayedGameUtils.RESULT_TYPE_CO_OP)) {
                imageTeamWinLoss.setVisibility(View.VISIBLE);
                txtRank.setVisibility(View.GONE);

                if (playerGameResultsList.get(i).getGameRank() == PlayedGameUtils.TEAM_LOST_RANK) {
                    imageTeamWinLoss.setImageResource(R.drawable.ic_sentiment_dissatisfied);
                } else {
                    imageTeamWinLoss.setImageResource(R.drawable.ic_sentiment_very_satisfied);
                }
            } else {
                imageTeamWinLoss.setVisibility(View.GONE);
                txtRank.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    txtRank.setText(Html.fromHtml(OrdinalIndicatorUtils.formatOrdinalIndicatorForNumber(currentGameRank), android.text.Html.FROM_HTML_MODE_LEGACY));
                } else {
                    txtRank.setText(Html.fromHtml(OrdinalIndicatorUtils.formatOrdinalIndicatorForNumber(currentGameRank)));
                }
            }

            String playersString = playerGameResultsList.get(i).getPlayerName();
            int j = i;
            while (j < listSize - 1) {
                int nextGameRank = playerGameResultsList.get(j + 1).getGameRank();
                if (currentGameRank == nextGameRank) {
                    playersString += "\n";
                    playersString += playerGameResultsList.get(j + 1).getPlayerName();
                } else {
                    break;
                }
                j++;
            }
            i = j;

            txtPlayerName.setText(playersString);
            if (currentGameRank == 1) {
                txtPlayerName.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                txtPlayerName.setTypeface(Typeface.DEFAULT);
            }

            holder.mContainerGameRankings.addView(gameResultRank);

            if (i == listSize - 1) {
                View connectingLineBottom = gameResultRank.findViewById(R.id.connecting_line_bottom);
                connectingLineBottom.setVisibility(View.GONE);
            }
            i++;
        }

        if (mPlayedGameViewModels.get(position).getGameDefinition() != null && mPlayedGameViewModels.get(position).getGameDefinition().getThumbnailLocalPath() != null) {
            mPicassoInstance
                    .load(new File(mPlayedGameViewModels.get(position).getGameDefinition().getThumbnailLocalPath()))
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
                mOnItemClickListener.onItemClicked(mPlayedGameViewModels.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayedGameViewModels.size();
    }

    @Override
    public long getHeaderId(int position) {
        try {
            return mHeadersPositions.get(position).getHeaderPosition();
        } catch (Exception ex) {
            return -1;
        }
    }

    @Override
    public HeaderHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.played_game_timeline_header, parent, false);
        return new PlayedGamesAdapter.HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(HeaderHolder headerHolder, int position) {
        if (DateUtils.isToday(mPlayedGameViewModels.get(position).getPlayedGame().getDatePlayed())) {
            headerHolder.mTxtDate.setText(mContext.getString(R.string.txt_today));
        } else if (DateUtils.isYesterday(mPlayedGameViewModels.get(position).getPlayedGame().getDatePlayed())) {
            headerHolder.mTxtDate.setText(mContext.getString(R.string.txt_yesterday));
        } else {
            headerHolder.mTxtDate.setText(mDateTimeFormatter.print(mPlayedGameViewModels.get(position).getPlayedGame().getDatePlayed()));
        }
        headerHolder.mTotalNemestatsPointsEarned.setText(String.format(mContext.getString(R.string.txt_symbol_nemestats_points), String.valueOf(mHeadersPositions.get(position).getTotalNemeStatsPoints())));
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        TextView mTxtDate;
        TextView mTotalNemestatsPointsEarned;

        public HeaderHolder(View itemView) {
            super(itemView);
            mTxtDate = itemView.findViewById(R.id.txt_date);
            mTotalNemestatsPointsEarned = itemView.findViewById(R.id.txt_total_nemestats_points_earned);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxtGameDefinitionName;
        public View mRootView;
        public LinearLayout mContainerGameRankings;

        public ImageView mImgThumbnailGameDefinition;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImgThumbnailGameDefinition = itemView.findViewById(R.id.img_thumbnail_game_definition);
            mTxtGameDefinitionName = itemView.findViewById(R.id.txt_game_definition_name);
            mContainerGameRankings = itemView.findViewById(R.id.container_game_rankings);
            mRootView = itemView.findViewById(R.id.root_view);

        }
    }

    public interface OnItemClickListener {
        void onItemClicked(PlayedGameViewModel playedGameViewModel);
    }
}


