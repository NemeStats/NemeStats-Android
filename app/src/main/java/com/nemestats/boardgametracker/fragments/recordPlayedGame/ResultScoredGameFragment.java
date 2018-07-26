package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.customViews.PlayerView;
import com.nemestats.boardgametracker.customViews.RankBoxView;
import com.nemestats.boardgametracker.customViews.ScoredPlayerResultView;
import com.nemestats.boardgametracker.customViews.drag.DragTouchListener;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

/**
 * Created by geomehedeniuc on 3/18/18.
 */

@EFragment(R.layout.fragment_result_scored_game_fragment)
public class ResultScoredGameFragment extends Fragment {

    @ViewById(R.id.container_players)
    LinearLayout mContainerPlayerScoredPoints;

    private RecordPlayedGameViewModel mViewModel;

    public static Fragment newInstance() {
        return new ResultScoredGameFragment_();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlayersUI();
        mViewModel.setGameResultType(PlayedGameUtils.RESULT_TYPE_SCORED);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            updatePlayersUI();
            mViewModel.setGameResultType(PlayedGameUtils.RESULT_TYPE_SCORED);
        }
    }


    private void updatePlayersUI() {
        mContainerPlayerScoredPoints.removeAllViews();
        List<PlayerViewModel> selectedPlayersList = mViewModel.getSelectedPlayers();
        for (PlayerViewModel playerViewModel : selectedPlayersList) {
            ScoredPlayerResultView scoredPlayerResultView = new ScoredPlayerResultView(getActivity(), playerViewModel);
            mContainerPlayerScoredPoints.addView(scoredPlayerResultView);
        }
    }


}
