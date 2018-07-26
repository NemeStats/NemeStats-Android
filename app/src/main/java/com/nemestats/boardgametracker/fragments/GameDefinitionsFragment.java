package com.nemestats.boardgametracker.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.NemeStatsMainActivity;
import com.nemestats.boardgametracker.activities.SearchGameDefinitionActivity;
import com.nemestats.boardgametracker.activities.SearchGameDefinitionActivity_;
import com.nemestats.boardgametracker.activities.GameDefinitionDetailsActivity;
import com.nemestats.boardgametracker.activities.GameDefinitionDetailsActivity_;
import com.nemestats.boardgametracker.adapters.GameDefinitionsAdapter;
import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsActionLocation;
import com.nemestats.boardgametracker.interfaces.OnDataChangedListener;
import com.nemestats.boardgametracker.viewModels.FragmentGameDefinitionsViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/24/17.
 */

@EFragment(R.layout.fragment_game_definitions)
public class GameDefinitionsFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_GAME_DEFINITION = 1626;
    private static final int REQUEST_CODE_GAME_DEFINITION_DETAILS = 1629;

    @ViewById(R.id.game_definitions_list)
    RecyclerView mGameDefinitionList;

    @ViewById(R.id.txt_empty_view)
    TextView mTxtEmptyView;

    @ViewById(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    FragmentGameDefinitionsViewModel mViewModel;

    private GameDefinitionsAdapter mGameDefinitionsAdapter;
    private FragmentGameDefinitionsViewModel.OnDataLoadedListener mOnDataLoadedListener;
    private GameDefinitionsAdapter.OnItemClickListener mOnItemClickListener;


    public static GameDefinitionsFragment newInstance() {
        return new GameDefinitionsFragment_();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setupUI();
    }

    public void onGameDefinitionUpdated(GameDefinition gameDefinition) {

    }

    @AfterViews
    public void setupViews() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (getActivity() != null) {
                ((NemeStatsMainActivity) getActivity()).logReloadContentEvent(AnalyticsActionLocation.GAMES_SCREEN);
            }
            mViewModel.triggerSync();
        });

        mOnItemClickListener = gameDefinitionViewModel -> {
            Intent intent = new Intent(getActivity(), GameDefinitionDetailsActivity_.class);
            intent.putExtra(GameDefinitionDetailsActivity.EXTRA_GAME_DEFINITION_SERVER_ID, gameDefinitionViewModel.getGameDefinition().getServerId());
            startActivityForResult(intent, REQUEST_CODE_GAME_DEFINITION_DETAILS);
        };

        mOnDataLoadedListener = new FragmentGameDefinitionsViewModel.OnDataLoadedListener() {
            @Override
            public void onDataLoaded() {
                setupUI();
            }

            @Override
            public void onThumbnailDownloaded(GameDefinition gameDefinition) {
                if (getActivity() != null && getActivity() instanceof OnDataChangedListener) {
                    ((OnDataChangedListener) getActivity()).onGameDefinitionUpdated(gameDefinition);
                }
                setupUI();
            }
        };

        mViewModel.setOnDataLoadedListener(mOnDataLoadedListener);

        if (mGameDefinitionsAdapter == null) {
            mGameDefinitionsAdapter = new GameDefinitionsAdapter(getActivity(), mViewModel.getGameDefinitionViewModelList(), mOnItemClickListener);
            mGameDefinitionList.setLayoutManager(new LinearLayoutManager(getActivity()));
            mGameDefinitionList.setAdapter(mGameDefinitionsAdapter);
            mGameDefinitionList.setItemAnimator(new DefaultItemAnimator());
            mGameDefinitionList.setHasFixedSize(true);
        }
        buildGameDefinitionData();
    }

    @Click(R.id.btn_create_game_definition)
    public void onBtnCreateGameDefinitionClick() {
        Intent intent = new Intent(getActivity(), SearchGameDefinitionActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_GAME_DEFINITION);
    }

    @UiThread
    public void setupUI() {
        if (mViewModel.getGameDefinitionViewModelList().isEmpty()) {
            mTxtEmptyView.setVisibility(View.VISIBLE);
        } else {
            mTxtEmptyView.setVisibility(View.GONE);
        }

        mViewModel.downloadRequiredThumbnailsForGameDefinitions();
        mGameDefinitionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.disposeObservables();
    }

    public void refreshDisplayedData(GamingGroup gamingGroup) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mViewModel.disposeObservables();
        buildGameDefinitionData();
    }

    @Background
    public void buildGameDefinitionData() {
        mViewModel.buildGameDefinitionData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_GAME_DEFINITION) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    GameDefinition extraGameDefinition = (GameDefinition) data.getSerializableExtra(SearchGameDefinitionActivity.EXTRA_GAME_DEFINITION);
                    mViewModel.addCreatedGameDefiniton(extraGameDefinition);
                    mGameDefinitionsAdapter.notifyDataSetChanged();
                }
            }
        }
        if (requestCode == REQUEST_CODE_GAME_DEFINITION_DETAILS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Intent broadcastIntent = new Intent(DataSyncBroadcastReceiver.FORCE_UPDATE_DATA);
                    LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(broadcastIntent);
                }
            }
        }

    }
}
