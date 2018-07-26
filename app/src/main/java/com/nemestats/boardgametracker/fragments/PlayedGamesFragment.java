package com.nemestats.boardgametracker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.NemeStatsMainActivity;
import com.nemestats.boardgametracker.activities.RecordPlayedGameActivity;
import com.nemestats.boardgametracker.activities.RecordPlayedGameActivity_;
import com.nemestats.boardgametracker.adapters.PlayedGamesAdapter;
import com.nemestats.boardgametracker.adapters.RecyclerViewItemDecoration.TimelineRecyclerViewDecoration;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionLocation;
import com.nemestats.boardgametracker.viewModels.PlayedGamesFragmentViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.BackgroundExecutor;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/24/17.
 */

@EFragment(R.layout.fragment_played_games)
public class PlayedGamesFragment extends Fragment {
    public static final String UPDATE_GAME_DEFINITION_DATA_TASK = "updateGameDefinitionDataTask";
    public static final String BUILD_PLAYED_GAMES_DATA_TASK = "buildPlayedGamesData";
    public static final int REQUEST_CODE_CREATE_PLAYED_GAME = 62;

    @ViewById(R.id.txt_empty_view)
    TextView mTxtEmptyView;

    @ViewById(R.id.played_games_list)
    RecyclerView mPlayedGamesList;

    @ViewById(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private PlayedGamesAdapter mPlayedGamesAdapter;
    private TimelineRecyclerViewDecoration mTimelineRecyclerViewDecoration;
    @Inject
    PlayedGamesFragmentViewModel mViewModel;
    private PlayedGamesAdapter.OnItemClickListener mOnItemClickListener;

    public static PlayedGamesFragment newInstance() {
        return new PlayedGamesFragment_();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Click(R.id.btn_record_played_game)
    public void onBtnRecordPlayedGameClick() {
        Intent intent = new Intent(getActivity(), RecordPlayedGameActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_PLAYED_GAME);
    }


    @AfterViews
    public void setupViews() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (getActivity() != null) {
                ((NemeStatsMainActivity) getActivity()).logReloadContentEvent(AnalyticsActionLocation.PLAYS_SCREEN);
            }
            mViewModel.triggerSync();
        });

        mOnItemClickListener = gameDefinitionViewModel -> {
            new MaterialDialog.Builder(getActivity()).title(gameDefinitionViewModel.getPlayedGame().getGameDefinitionName())
                    .content(getString(R.string.view_on_nemestats_com) + "?")
                    .positiveText(android.R.string.yes)
                    .onPositive((dialog, which) -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gameDefinitionViewModel.getPlayedGame().getNemestatsUrl()));
                        startActivity(browserIntent);
                    }).neutralText(android.R.string.no).show();
        };

        mViewModel.setOnDataLoadedListener(this::setupUI);

        if (mPlayedGamesAdapter == null) {
            mPlayedGamesAdapter = new PlayedGamesAdapter(getActivity(), mViewModel.getPlayedGamesViewModelList(), mViewModel.getHeadersPosition(), mOnItemClickListener);
            mPlayedGamesList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mTimelineRecyclerViewDecoration = new TimelineRecyclerViewDecoration(mPlayedGamesAdapter, false, getActivity());
            mPlayedGamesList.addItemDecoration(mTimelineRecyclerViewDecoration);
            mPlayedGamesList.setAdapter(mPlayedGamesAdapter);
            mPlayedGamesList.setItemAnimator(new DefaultItemAnimator());
            mPlayedGamesList.setHasFixedSize(true);
        }
        buildPlayedGamedData(true);
    }

    @Background(id = BUILD_PLAYED_GAMES_DATA_TASK, serial = "shouldBeSerial")
    public void buildPlayedGamedData(boolean refreshFromDatabase) {
        mViewModel.buildPlayedGamesData(refreshFromDatabase);
    }

    @Background(id = UPDATE_GAME_DEFINITION_DATA_TASK, serial = "shouldBeSerial")
    public void onGameDefinitionUpdated(GameDefinition gameDefinition) {
        mViewModel.onGameDefinitionUpdated(gameDefinition);
    }

    @UiThread
    public void setupUI(boolean smoothScrollToPosition, int position) {
        if (mViewModel.getPlayedGamesViewModelList().isEmpty()) {
            mTxtEmptyView.setVisibility(View.VISIBLE);
        } else {
            mTxtEmptyView.setVisibility(View.GONE);
        }
        mTimelineRecyclerViewDecoration.clearHeaderCache();
        if (smoothScrollToPosition) {
            mPlayedGamesAdapter.notifyItemInserted(position);
            mPlayedGamesList.smoothScrollToPosition(position);
        } else {
            mPlayedGamesAdapter.notifyDataSetChanged();
        }
    }


    public void refreshDisplayedData(GamingGroup gamingGroup) {
        mSwipeRefreshLayout.setRefreshing(false);
        BackgroundExecutor.cancelAll(UPDATE_GAME_DEFINITION_DATA_TASK, true);
        BackgroundExecutor.cancelAll(BUILD_PLAYED_GAMES_DATA_TASK, true);
        buildPlayedGamedData(true);
        mPlayedGamesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_PLAYED_GAME && resultCode == Activity.RESULT_OK) {
            if (data != null && data.hasExtra(RecordPlayedGameActivity.EXTRA_PLAYED_GAME)) {
                PlayedGame playedGame = (PlayedGame) data.getSerializableExtra(RecordPlayedGameActivity.EXTRA_PLAYED_GAME);
                if (playedGame != null) {
                    mViewModel.onPlayedGameCreated(playedGame);
                    buildPlayedGamedData(false);
                }
            }
        }
    }
}
