package com.nemestats.boardgametracker.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.NemeStatsMainActivity;
import com.nemestats.boardgametracker.adapters.GamingGroupsAdapter;
import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.viewModels.GamingGroupFragmentViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GamingGroupViewModel;

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

@EFragment(R.layout.fragment_gaming_groups)
public class GamingGroupsFragment extends Fragment {

    @ViewById(R.id.gaming_groups_list)
    RecyclerView mGamingGroupsList;

    @ViewById(R.id.txt_empty_view)
    View mTxtEmptyView;

    @ViewById(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Inject
    GamingGroupFragmentViewModel mViewModel;

    private GamingGroupsAdapter mGamingGroupsAdapter;
    private GamingGroupsAdapter.OnGamingGroupEventsListener mOnGamingGroupEventsListener;

    private GamingGroupFragmentViewModel.OnDataLoadedListener mOnDataLoadedListener;

    private DataSyncBroadcastReceiver.OnDataSyncFinishedListener mOnDataSyncFinishedListener;
    private DataSyncBroadcastReceiver mDataSyncBroadcastReceiver;

    public static GamingGroupsFragment newInstance() {
        return new GamingGroupsFragment_();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NemeStatsApplication.getAppGraph().inject(this);
        registerDataSyncBroadcastReceiver();
    }

    @Click(R.id.btn_create_gaming_group)
    public void onBtnCreateGamingGroup() {
        try {
            String editGaminGroupUrl = "https://nemestats.com/Account/Manage#gaming-groups";
            Intent editGamingGroupIntent = new Intent(Intent.ACTION_VIEW);
            editGamingGroupIntent.setData(Uri.parse(editGaminGroupUrl));
            startActivity(editGamingGroupIntent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "No browser found on your android device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterDataSyncBroadcastReceiver();
    }

    @AfterViews
    public void setupViews() {
        mSwipeRefreshLayout.setOnRefreshListener(() -> mViewModel.triggerSync());

        mOnDataLoadedListener = this::setupUI;

        mViewModel.setOnDataLoadedListener(mOnDataLoadedListener);

        mOnGamingGroupEventsListener = new GamingGroupsAdapter.OnGamingGroupEventsListener() {
            @Override
            public void onGamingGroupClicked(GamingGroupViewModel gamingGroupViewModel) {
                Toast.makeText(getActivity(), gamingGroupViewModel.getGamingGroup().getGroupName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onGamingGroupMenuEdit(GamingGroupViewModel gamingGroupViewModel) {
                onEditGamingGroup(gamingGroupViewModel.getGamingGroup());
            }

            @Override
            public void onGamingGroupMenuDelete(GamingGroupViewModel gamingGroupViewModel) {
                onEditGamingGroup(gamingGroupViewModel.getGamingGroup());
            }

            @Override
            public void onGamingGroupMenuViewOnNemestats(GamingGroupViewModel gamingGroupViewModel) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gamingGroupViewModel.getGamingGroup().getNemestatsUrl()));
                startActivity(browserIntent);
            }
        };

        mGamingGroupsAdapter = new GamingGroupsAdapter(getActivity(), mViewModel.getGamingGroupViewModelList(), mOnGamingGroupEventsListener);
        mGamingGroupsList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGamingGroupsList.setAdapter(mGamingGroupsAdapter);
        mGamingGroupsList.setItemAnimator(new DefaultItemAnimator());
        buildGamingGroupsViewModelList();
    }


    @Background
    public void buildGamingGroupsViewModelList() {
        mViewModel.buildGamingGroupsViewModelList();
    }

    @UiThread
    public void setupUI() {
        if (mViewModel.getGamingGroupViewModelList().isEmpty()) {
            mTxtEmptyView.setVisibility(View.VISIBLE);
            mGamingGroupsList.setVisibility(View.GONE);
        } else {
            mTxtEmptyView.setVisibility(View.GONE);
            mGamingGroupsList.setVisibility(View.VISIBLE);
        }

        mGamingGroupsAdapter.notifyDataSetChanged();
    }


    public void onEditGamingGroup(GamingGroup gamingGroup) {
        try {
            String editGaminGroupUrl = "https://nemestats.com/GamingGroup/Edit/";
            Intent editGamingGroupIntent = new Intent(Intent.ACTION_VIEW);
            editGamingGroupIntent.setData(Uri.parse(editGaminGroupUrl + gamingGroup.getServerId()));
            startActivity(editGamingGroupIntent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "No browser found on your android device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void refreshDisplayedData() {
        mSwipeRefreshLayout.setRefreshing(false);
        buildGamingGroupsViewModelList();
    }

    private void registerDataSyncBroadcastReceiver() {
        mOnDataSyncFinishedListener = new DataSyncBroadcastReceiver.OnDataSyncFinishedListener() {
            @Override
            public void onDataSyncFinishedSuccess() {
                refreshDisplayedData();
            }

            @Override
            public void onDataSyncFinishedFail() {
                refreshDisplayedData();
            }

            @Override
            public void onForceUpdateData() {
                refreshDisplayedData();
            }
        };

        mDataSyncBroadcastReceiver = new DataSyncBroadcastReceiver(mOnDataSyncFinishedListener);

        IntentFilter cloudSyncIntents = new IntentFilter();
        cloudSyncIntents.addAction(DataSyncBroadcastReceiver.DATA_SYNC_FINISHED_FAIL);
        cloudSyncIntents.addAction(DataSyncBroadcastReceiver.DATA_SYNC_FINISHED_SUCCESS);
        cloudSyncIntents.addAction(DataSyncBroadcastReceiver.FORCE_UPDATE_DATA);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDataSyncBroadcastReceiver, cloudSyncIntents);
    }

    private void unregisterDataSyncBroadcastReceiver() {
        if (mDataSyncBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDataSyncBroadcastReceiver);
        }
    }
}
