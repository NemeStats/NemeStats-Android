package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.customViews.FlowLayout;
import com.nemestats.boardgametracker.customViews.FlowLayoutGravityRight;
import com.nemestats.boardgametracker.customViews.RankBoxView;
import com.nemestats.boardgametracker.customViews.drag.DragListener;
import com.nemestats.boardgametracker.customViews.drag.DragTouchListener;
import com.nemestats.boardgametracker.customViews.PlayerView;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by geomehedeniuc on 3/18/18.
 */

@EFragment(R.layout.fragment_result_ranked_game)
public class ResultRankedGameFragment extends Fragment {

    @ViewById(R.id.container_player_results)
    FlowLayoutGravityRight mContainerPlayerResults;

    @ViewById(R.id.flow_layout)
    FlowLayout mFlowLayout;

    public static final int NUMBER_RANK_BOXES_PER_LINE = 2;

    private RecordPlayedGameViewModel mViewModel;
    private int mScreenWidth;
    private DragListener.OnViewDropListener mOnViewDropListener;

    public static Fragment newInstance() {
        return new ResultRankedGameFragment_();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
        initScreenSpecs();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setGameResultType(PlayedGameUtils.RESULT_TYPE_RANKED);
        setupRankBoxes();
        updatePlayersUI();
    }

    private void initScreenSpecs() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }

    @AfterViews
    public void setupViews() {
        mOnViewDropListener = (rankBoxView, player) -> {
            PlayerViewModel playerViewModel = player.getPlayerViewModel();
            int rank = rankBoxView.getRank();
            playerViewModel.setRankAssigned(true);
            playerViewModel.setRank(rank);
        };
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setupRankBoxes();
            updatePlayersUI();
            mViewModel.setGameResultType(PlayedGameUtils.RESULT_TYPE_RANKED);
        }
    }

    private void setupRankBoxes() {
        mFlowLayout.removeAllViews();
        int numberOfSelectedPlayers = mViewModel.getSelectedPlayers().size();

        int horizontalBoxesSpacing = (int) getResources().getDimension(R.dimen.dimen_vertical_spacing_selected_players);
        int widthRankBox = mScreenWidth / NUMBER_RANK_BOXES_PER_LINE - horizontalBoxesSpacing * 3;

        for (int i = 0; i < numberOfSelectedPlayers; i++) {
            RankBoxView rankBoxView = new RankBoxView(getActivity(), i);
            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(widthRankBox, ViewGroup.LayoutParams.WRAP_CONTENT);
            rankBoxView.setLayoutParams(layoutParams);
            rankBoxView.setMinimumHeight(widthRankBox / 2);
            mFlowLayout.addView(rankBoxView);
            rankBoxView.setOnDragListener(new DragListener(mOnViewDropListener));
        }
    }

    private void updatePlayersUI() {
        mContainerPlayerResults.removeAllViews();

        List<PlayerViewModel> selectedPlayersList = mViewModel.getSelectedPlayers();
        for (PlayerViewModel playerViewModel : selectedPlayersList) {

            PlayerView playerView = new PlayerView(getActivity(), playerViewModel, null, false, false);
            playerView.setTag(playerViewModel.getPlayer().getServerId());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int margins = (int) getResources().getDimension(R.dimen.dimen_vertical_spacing_selected_players) / 2;
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(margins, margins, margins, margins);
            playerView.setOnTouchListener(new DragTouchListener());

            if (playerViewModel.isRankAssigned()) {
                playerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                RankBoxView rankBox = (RankBoxView) mFlowLayout.getChildAt(playerViewModel.getAssignedRank());
                if (rankBox != null) {
                    LinearLayout rankboxPlayersContainer = rankBox.findViewById(R.id.container_players);
                    rankboxPlayersContainer.addView(playerView, rankboxPlayersContainer.getChildCount());
                    playerView.setVisibility(View.VISIBLE);
                } else {
                    playerViewModel.setRankAssigned(false);
                    playerViewModel.setRank(-1);
                    mContainerPlayerResults.addView(playerView, layoutParams);
                }
            } else {
                playerViewModel.setRankAssigned(false);
                playerViewModel.setRank(-1);
                mContainerPlayerResults.addView(playerView, layoutParams);
            }
        }
    }


}
