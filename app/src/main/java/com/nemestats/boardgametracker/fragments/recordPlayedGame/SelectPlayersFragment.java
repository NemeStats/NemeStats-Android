package com.nemestats.boardgametracker.fragments.recordPlayedGame;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.activities.EditPlayerActivity;
import com.nemestats.boardgametracker.activities.EditPlayerActivity_;
import com.nemestats.boardgametracker.adapters.PlayersAdapter;
import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.customViews.FlowLayout;
import com.nemestats.boardgametracker.customViews.LinearLayoutWithKeyboardListener;
import com.nemestats.boardgametracker.customViews.PlayerView;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.interfaces.OnCreatePlayedGameEventsListener;
import com.nemestats.boardgametracker.viewModels.RecordPlayedGameViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by geomehedeniuc on 3/13/18.
 */

@EFragment(R.layout.fragment_select_players)
public class SelectPlayersFragment extends Fragment {

    private static final int REQUEST_CODE_CREATE_PLAYER = 6201;
    @ViewById(R.id.players_list)
    RecyclerView mSelectedPlayersList;

    @ViewById(R.id.txt_empty_view)
    View mEmptyView;

    @ViewById(R.id.edt_search_players)
    EditText mEdtSearchPlayers;

    @ViewById(R.id.flow_layout)
    FlowLayout mFlowLayout;

    @ViewById(R.id.selected_players_scroll_view)
    ScrollView mSelectedPlayersScrollView;

    @ViewById(R.id.players_not_selected_view)
    View mPleaseSelectPlayers;

    @ViewById(R.id.btn_next)
    View mBtnNext;

    @ViewById(R.id.btn_previous)
    View mBtnPrevious;

    @ViewById(R.id.top_container)
    View mTopContainer;

    @ViewById(R.id.next_prev_layout)
    View mNextPrevView;

    @ViewById(R.id.shadow_bottom_navigation)
    View mShadowBottomNavigation;

    @ViewById(R.id.btn_create_player)
    View mBtnCreatePlayer;

    @ViewById(R.id.keyboard_listener_view)
    LinearLayoutWithKeyboardListener mLinearLayoutWithKeyboardListener;

    private RecordPlayedGameViewModel mViewModel;

    private PlayersAdapter mPlayersAdapter;
    private PlayersAdapter.OnPlayerClickListener mOnPlayerClickListener;
    private BehaviorSubject<Object> mEdtSearchGamesBehaviorSubject;
    private PlayerView.OnButtonRemoveListener mOnButtonRemoveListener;

    private OnCreatePlayedGameEventsListener mOnCreatePlayedGameEventsListener;


    public static SelectPlayersFragment newInstance() {
        return new SelectPlayersFragment_();
    }

    public void onPageSelected(boolean isThisCurrentPage) {
        if (isThisCurrentPage) {
            hideKeyboard();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = RecordPlayedGameViewModel.getInstance();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCreatePlayedGameEventsListener) {
            mOnCreatePlayedGameEventsListener = (OnCreatePlayedGameEventsListener) context;
        }
    }


    @Click(R.id.btn_create_player)
    public void onBtnAddPlayer() {
        mEdtSearchPlayers.setText("");
        Intent intent = new Intent(getActivity(), EditPlayerActivity_.class);
        startActivityForResult(intent, REQUEST_CODE_CREATE_PLAYER);
    }


    @Click(R.id.btn_previous)
    public void onBtnPreviousClick() {
        if (mOnCreatePlayedGameEventsListener != null) {
            mOnCreatePlayedGameEventsListener.onBtnPreviousClick();
        }
    }

    @Click(R.id.btn_next)
    public void onBtnNextClick() {
        if (mOnCreatePlayedGameEventsListener != null) {
            mOnCreatePlayedGameEventsListener.onBtnNextClick();
        }
    }

    @AfterViews
    public void setupViews() {
        mLinearLayoutWithKeyboardListener.addKeyboardStateChangedListener(new LinearLayoutWithKeyboardListener.IKeyboardChanged() {
            @Override
            public void onKeyboardShown(int actualHeight, int proposedheight) {
                mTopContainer.setVisibility(View.GONE);
                mNextPrevView.setVisibility(View.GONE);
                mShadowBottomNavigation.setVisibility(View.GONE);
                mBtnCreatePlayer.setVisibility(View.GONE);
            }

            @Override
            public void onKeyboardHidden(int actualHeight, int proposedheight) {
                mTopContainer.setVisibility(View.VISIBLE);
                mNextPrevView.setVisibility(View.VISIBLE);
                mShadowBottomNavigation.setVisibility(View.VISIBLE);
                mBtnCreatePlayer.setVisibility(View.VISIBLE);
            }
        });

        mEdtSearchPlayers.setCompoundDrawablesRelativeWithIntrinsicBounds(AppCompatResources.getDrawable(getActivity(), R.drawable.ic_search), null, null, null);

        final int minHeightSelectedPlayersContainer = (int) getResources().getDimension(R.dimen.dimen_min_height_selected_players_container);
        LayoutTransition lt = new LayoutTransition();
        lt.disableTransitionType(LayoutTransition.APPEARING);
        mFlowLayout.setLayoutTransition(lt);
        mFlowLayout.setOnSizeChangedListener((w, h, oldw, oldh) -> {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Math.min(Math.max(h, minHeightSelectedPlayersContainer), minHeightSelectedPlayersContainer * 3)
            );
            mSelectedPlayersScrollView.setLayoutParams(layoutParams);
            mSelectedPlayersScrollView.setClipChildren(false);
            mSelectedPlayersScrollView.setClipToPadding(false);
        });

        mOnPlayerClickListener = (playerViewModel, position) -> {
            mViewModel.onPlayerClick(playerViewModel);
            mPlayersAdapter.notifyItemChanged(position);
            updateSelectedPlayers();
            mEdtSearchPlayers.setText("");
        };

        mPlayersAdapter = new PlayersAdapter(getActivity(), mViewModel.getPlayersInGamingGroup(), mOnPlayerClickListener);
        mSelectedPlayersList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSelectedPlayersList.setAdapter(mPlayersAdapter);


        mEdtSearchGamesBehaviorSubject = BehaviorSubject.create();

        mEdtSearchGamesBehaviorSubject
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s ->
                        mViewModel.searchLocalPlayers(String.valueOf(s))
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        updatePlayersList();
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }
                                }));

        mEdtSearchPlayers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                mEdtSearchGamesBehaviorSubject.onNext(String.valueOf(text));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        updatePlayersList();

        if (mOnButtonRemoveListener == null) {
            mOnButtonRemoveListener = playerViewModel -> {

                mViewModel.onPlayerClick(playerViewModel);
                mPlayersAdapter.notifyDataSetChanged();
                updateSelectedPlayers();
            };
        }
    }

    private void updateSelectedPlayers() {
        List<PlayerViewModel> selectedPlayersList = mViewModel.getSelectedPlayers();

        for (int i = 0; i < mFlowLayout.getChildCount(); i++) {
            View child = mFlowLayout.getChildAt(i);
            boolean shouldRemoveChild = true;

            for (PlayerViewModel playerViewModel : selectedPlayersList) {
                if (child.getTag().equals(playerViewModel.getPlayer().getServerId())) {
                    if (playerViewModel.isSelected()) {
                        shouldRemoveChild = false;
                        break;
                    }
                }
            }

            if (shouldRemoveChild) {
                ViewCompat.animate(child).scaleX(.0f).scaleY(.0f).setDuration(250).setInterpolator(new AnticipateInterpolator())
                        .withEndAction(() -> mFlowLayout.removeView(child))
                        .start();
            }
        }

        for (PlayerViewModel playerViewModel : selectedPlayersList) {
            boolean alreadyAdded = false;
            for (int i = 0; i < mFlowLayout.getChildCount(); i++) {
                View child = mFlowLayout.getChildAt(i);
                if (child.getTag().equals(playerViewModel.getPlayer().getServerId())) {
                    alreadyAdded = true;
                    break;
                }
            }
            if (!alreadyAdded) {
                PlayerView playerView = new PlayerView(getActivity(), playerViewModel, mOnButtonRemoveListener, true, true);
                playerView.setTag(playerViewModel.getPlayer().getServerId());
                FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mFlowLayout.addView(playerView, layoutParams);
            }
        }
        if (selectedPlayersList.isEmpty()) {
            mSelectedPlayersScrollView.setVisibility(View.GONE);
            mPleaseSelectPlayers.setVisibility(View.VISIBLE);
        } else {
            mSelectedPlayersScrollView.setVisibility(View.VISIBLE);
            mPleaseSelectPlayers.setVisibility(View.GONE);
        }

        mFlowLayout.post(() -> {
            mSelectedPlayersScrollView.fullScroll(View.FOCUS_DOWN);
            mSelectedPlayersScrollView.fullScroll(View.FOCUS_DOWN);
        });
    }

    private void updatePlayersList() {
        if (mViewModel.getPlayersInGamingGroup().isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        mPlayersAdapter.notifyDataSetChanged();
    }

    public void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && this.getActivity().getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(this.getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_PLAYER) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Player player = (Player) data.getSerializableExtra(EditPlayerActivity.EXTRA_PLAYER);
                    PlayerViewModel playerViewModel = mViewModel.addCreatedPlayer(player);
                    mViewModel.onPlayerClick(playerViewModel);
                    updateSelectedPlayers();
                    mPlayersAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
