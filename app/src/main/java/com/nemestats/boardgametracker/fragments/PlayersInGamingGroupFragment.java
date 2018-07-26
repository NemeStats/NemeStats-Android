package com.nemestats.boardgametracker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.EditPlayerActivity;
import com.nemestats.boardgametracker.activities.EditPlayerActivity_;
import com.nemestats.boardgametracker.activities.NemeStatsMainActivity;
import com.nemestats.boardgametracker.activities.PlayerDetailsActivity;
import com.nemestats.boardgametracker.activities.PlayerDetailsActivity_;
import com.nemestats.boardgametracker.adapters.PlayersAdapter;
import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionLocation;
import com.nemestats.boardgametracker.viewModels.PlayersInGamingGroupFragmentViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by geomehedeniuc on 5/10/18.
 */

@EFragment(R.layout.fragment_players_in_selected_gaming_group)
public class PlayersInGamingGroupFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_PLAYER = 1621;
    private static final int REQUEST_CODE_PLAYER_DETAILS = 1622;

    @ViewById(R.id.players_list)
    RecyclerView mPlayersList;

    @ViewById(R.id.txt_empty_view)
    View mEmptyView;

    @ViewById(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    PlayersInGamingGroupFragmentViewModel mViewModel;
    private PlayersAdapter mPlayersAdapter;
    private PlayersAdapter.OnPlayerClickListener mOnPlayerClickListener;
    private PlayersInGamingGroupFragmentViewModel.OnDataLoadedListener mOnDataLoadedListener;


    public static PlayersInGamingGroupFragment newInstance() {
        return new PlayersInGamingGroupFragment_();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
    }

    @AfterViews
    public void setupViews() {
        mOnPlayerClickListener = (playerViewModel, position) -> {
            Intent intent = new Intent(getActivity(), PlayerDetailsActivity_.class);
            intent.putExtra(PlayerDetailsActivity.EXTRA_PLAYER, playerViewModel.getPlayer());
            startActivityForResult(intent, REQUEST_CODE_PLAYER_DETAILS);
        };

        mOnDataLoadedListener = this::setupUI;
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (getActivity() != null) {
                ((NemeStatsMainActivity) getActivity()).logReloadContentEvent(AnalyticsActionLocation.PLAYERS_SCREEN);
            }
            mViewModel.triggerSync();
        });

        mPlayersAdapter = new PlayersAdapter(getActivity(), mViewModel.getPlayersInGamingGroup(), mOnPlayerClickListener);
        mPlayersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPlayersList.setAdapter(mPlayersAdapter);

        mViewModel.setOnDataLoadedListener(mOnDataLoadedListener);
        buildGamingGroupsViewModelList();
    }

    @Click(R.id.btn_add_player)
    public void onBtnAddPlayer() {
        Intent intent = new Intent(getActivity(), EditPlayerActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_PLAYER);
    }


    @Background
    public void buildGamingGroupsViewModelList() {
        mViewModel.buildPlayerViewModelList();
    }


    @UiThread
    public void setupUI() {
        if (mViewModel.getPlayersInGamingGroup().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mPlayersList.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mPlayersList.setVisibility(View.VISIBLE);
        }
        mPlayersAdapter.notifyDataSetChanged();
    }

    public void onGameDefinitionUpdated(GameDefinition gameDefinition) {

    }

    public void refreshDisplayedData(GamingGroup gamingGroup) {
        mSwipeRefreshLayout.setRefreshing(false);
        buildGamingGroupsViewModelList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_PLAYER) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Player player = (Player) data.getSerializableExtra(EditPlayerActivity.EXTRA_PLAYER);
                    mViewModel.addCreatedPlayer(player);
                    mPlayersAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_CODE_PLAYER_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Intent broadcastIntent = new Intent(DataSyncBroadcastReceiver.FORCE_UPDATE_DATA);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(broadcastIntent);
                }
            }
        }

    }
}
