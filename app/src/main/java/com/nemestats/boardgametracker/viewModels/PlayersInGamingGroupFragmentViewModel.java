package com.nemestats.boardgametracker.viewModels;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.PlayersManager;
import com.nemestats.boardgametracker.managers.SyncManager;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GameDefinitionViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by geomehedeniuc on 5/10/18.
 */

public class PlayersInGamingGroupFragmentViewModel {

    private OnDataLoadedListener mOnDataLoadedListener;

    private SyncManager mSyncManager;
    private AccountManager mAccountManager;
    private PlayersManager mPlayersManager;

    private List<PlayerViewModel> mPlayerViewModelList;

    @Inject
    public PlayersInGamingGroupFragmentViewModel(SyncManager syncManager, AccountManager accountManager, PlayersManager playersManager) {
        mSyncManager = syncManager;
        mPlayersManager = playersManager;
        mAccountManager = accountManager;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener onDataLoadedListener) {
        mOnDataLoadedListener = onDataLoadedListener;
    }

    public List<PlayerViewModel> getPlayersInGamingGroup() {
        if (mPlayerViewModelList == null) {
            mPlayerViewModelList = new ArrayList<>();
        }
        return mPlayerViewModelList;
    }

    public void buildPlayerViewModelList() {
        List<PlayerViewModel> playerViewModels = new ArrayList<>();

        String selectedGamingGroupServerId = mAccountManager.getSelectedGamingGroupServerId();
        List<Player> playerList = mPlayersManager.getLocalPlayersInGamingGroup(selectedGamingGroupServerId,true);
        for (Player player : playerList) {
            PlayerViewModel playerViewModel = new PlayerViewModel(player);
            playerViewModels.add(playerViewModel);
        }

        mPlayerViewModelList.clear();
        mPlayerViewModelList.addAll(playerViewModels);

        if (mOnDataLoadedListener != null) {
            mOnDataLoadedListener.onDataLoaded();
        }
    }

    public void triggerSync() {
        mSyncManager.triggerSyncData(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void addCreatedPlayer(Player player) {
        if (player != null) {
            PlayerViewModel playerViewModel = new PlayerViewModel(player);
            mPlayerViewModelList.add(binarySearch(player), playerViewModel);

            if (mOnDataLoadedListener != null) {
                mOnDataLoadedListener.onDataLoaded();
            }
        }
    }

    private int binarySearch(Player player) {
        int low = 0;
        int high = mPlayerViewModelList.size() - 1;
        while (high >= low) {
            int middle = (low + high) / 2;
            if (mPlayerViewModelList.get(middle).getPlayer().getPlayerName().compareToIgnoreCase(player.getPlayerName()) > 1) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return low;
    }

    public void updatePlayer(Player player) {
        for (PlayerViewModel playerViewModel : mPlayerViewModelList) {
            if (playerViewModel.getPlayer().getServerId() == player.getServerId()) {
                playerViewModel.setPlayer(player);
            }
        }
    }

    public interface OnDataLoadedListener {
        void onDataLoaded();
    }
}
