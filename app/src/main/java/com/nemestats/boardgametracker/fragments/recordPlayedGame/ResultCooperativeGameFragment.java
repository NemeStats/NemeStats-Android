package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

/**
 * Created by geomehedeniuc on 3/18/18.
 */

@EFragment(R.layout.fragment_result_cooperative_game)
public class ResultCooperativeGameFragment extends Fragment {

    @ViewById(R.id.btn_everyone_lost)
    View mBtnEveryOneLost;

    @ViewById(R.id.btn_everyone_won)
    View mBtnEveryOneWon;

    private RecordPlayedGameViewModel mViewModel;

    public static Fragment newInstance() {
        return new ResultCooperativeGameFragment_();
    }

    @Click(R.id.btn_everyone_won)
    public void onBtnEveryoneWonClick() {
        mViewModel.setCOOPResult(PlayedGameUtils.TEAM_WIN_RANK);
        setupSelectedButtons();
    }

    @Click(R.id.btn_everyone_lost)
    public void onBtnEveryoneLostClick() {
        mViewModel.setCOOPResult(PlayedGameUtils.TEAM_LOST_RANK);
        setupSelectedButtons();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.setGameResultType(PlayedGameUtils.RESULT_TYPE_CO_OP);
    }

    @AfterViews
    public void setupViews() {
        setupSelectedButtons();
    }

    private void setupSelectedButtons() {
        mBtnEveryOneLost.setSelected(false);
        mBtnEveryOneWon.setSelected(false);
        if (mViewModel.getCOOPResult() == PlayedGameUtils.TEAM_LOST_RANK) {
            mBtnEveryOneLost.setSelected(true);
        } else if (mViewModel.getCOOPResult() == PlayedGameUtils.TEAM_WIN_RANK) {
            mBtnEveryOneWon.setSelected(true);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            mViewModel.setGameResultType(PlayedGameUtils.RESULT_TYPE_CO_OP);
        }
    }


}
