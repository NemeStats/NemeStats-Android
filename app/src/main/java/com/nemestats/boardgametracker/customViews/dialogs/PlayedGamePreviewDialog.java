package com.nemestats.boardgametracker.customViews.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.utils.DateUtils;
import com.nemestats.boardgametracker.utils.OrdinalIndicatorUtils;
import com.nemestats.boardgametracker.utils.PicassoRoundedCornerTransformation;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.squareup.picasso.Picasso;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;


/**
 * Created by geomehedeniuc on 4/26/18.
 */

public class PlayedGamePreviewDialog extends MaterialDialog {

    private final DateTimeFormatter mDateTimeFormatter;
    private PlayedGame mPlayedGame;
    private GameDefinition mGameDefinition;
    private Picasso mPicassoInstance;

    private TextView mTxtGameDefinitionName;
    private View mRootView;
    private LinearLayout mContainerGameRankings;
    private ImageView mImgThumbnailGameDefinition;
    private TextView mTextPlayedDate;
    private TextView mTextGameNotes;

    public PlayedGamePreviewDialog(Builder builder, PlayedGame playedGame, GameDefinition gameDefinition) {
        super(builder);
        mPlayedGame = playedGame;
        mGameDefinition = gameDefinition;
        mDateTimeFormatter = DateTimeFormat.forPattern("dd MMM yyyy");
        initViews();
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setDimAmount(.7f);
        }
    }

    private void initViews() {
        View customView = getCustomView();
        mImgThumbnailGameDefinition = customView.findViewById(R.id.img_thumbnail_game_definition);
        mTxtGameDefinitionName = customView.findViewById(R.id.txt_game_definition_name);
        mContainerGameRankings = customView.findViewById(R.id.container_game_rankings);
        mRootView = customView.findViewById(R.id.root_view);
        mTextPlayedDate = customView.findViewById(R.id.txt_game_played_date);
        mTextGameNotes = customView.findViewById(R.id.txt_game_notes);

        mPicassoInstance = Picasso.with(getContext());
        bindViews();
    }

    private void bindViews() {
        mTxtGameDefinitionName.setText(getPlayedGame().getGameDefinitionName());

        if (DateUtils.isToday(getPlayedGame().getDatePlayed())) {
            mTextPlayedDate.setText(getContext().getString(R.string.txt_today));
        } else if (DateUtils.isYesterday(getPlayedGame().getDatePlayed())) {
            mTextPlayedDate.setText(getContext().getString(R.string.txt_yesterday));
        } else {
            mTextPlayedDate.setText(mDateTimeFormatter.print(getPlayedGame().getDatePlayed()));
        }

        if (getPlayedGame().getNotes() != null && !getPlayedGame().getNotes().isEmpty()) {
            mTextGameNotes.setText(getPlayedGame().getNotes());
        } else {
            mTextGameNotes.setText("-");
        }

        mContainerGameRankings.removeAllViews();
        int listSize = getPlayedGame().getPlayerGameResultsList().size();
        int i = 0;
        while (i < listSize) {

            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gameResultRank = layoutInflater.inflate(R.layout.game_result_rank, null);

            if (i == 0) {
                View connectingLineTop = gameResultRank.findViewById(R.id.connecting_line_top);
                connectingLineTop.setVisibility(View.GONE);
            }

            TextView txtRank = gameResultRank.findViewById(R.id.txt_badge_rank_count);

            TextView txtPlayerName = gameResultRank.findViewById(R.id.txt_player_name);

            AppCompatImageView imageTeamWinLoss = gameResultRank.findViewById(R.id.img_team_win_loss);

            int currentGameRank = getPlayedGame().getPlayerGameResultsList().get(i).getGameRank();

            if (getPlayedGame().getGameResultType().equals(PlayedGameUtils.RESULT_TYPE_CO_OP)) {
                imageTeamWinLoss.setVisibility(View.VISIBLE);
                txtRank.setVisibility(View.GONE);

                if (getPlayedGame().getPlayerGameResultsList().get(i).getGameRank() == PlayedGameUtils.TEAM_LOST_RANK) {
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

            String playersString = getPlayedGame().getPlayerGameResultsList().get(i).getPlayerName();
            int j = i;
            while (j < listSize - 1) {
                int nextGameRank = getPlayedGame().getPlayerGameResultsList().get(j + 1).getGameRank();
                if (currentGameRank == nextGameRank) {
                    playersString += "\n";
                    playersString += getPlayedGame().getPlayerGameResultsList().get(j + 1).getPlayerName();
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

            mContainerGameRankings.addView(gameResultRank);

            if (i == listSize - 1) {
                View connectingLineBottom = gameResultRank.findViewById(R.id.connecting_line_bottom);
                connectingLineBottom.setVisibility(View.GONE);
            }
            i++;
        }

        if (mGameDefinition.getThumbnailLocalPath() != null) {
            mPicassoInstance
                    .load(new File(mGameDefinition.getThumbnailLocalPath()))
                    .transform(new PicassoRoundedCornerTransformation())
                    .placeholder(R.drawable.placeholder_image)
                    .into(mImgThumbnailGameDefinition);
        } else {
            mPicassoInstance
                    .load(R.drawable.placeholder_image)
                    .transform(new PicassoRoundedCornerTransformation())
                    .into(mImgThumbnailGameDefinition);
        }

    }

    private PlayedGame getPlayedGame() {
        return mPlayedGame;
    }
}
