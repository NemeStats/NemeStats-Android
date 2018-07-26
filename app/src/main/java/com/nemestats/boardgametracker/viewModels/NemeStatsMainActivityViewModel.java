package com.nemestats.boardgametracker.viewModels;

import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.domain.UserPlayer;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.GamingGroupsManager;
import com.nemestats.boardgametracker.managers.PlayersManager;
import com.nemestats.boardgametracker.managers.SyncManager;
import com.nemestats.boardgametracker.managers.UserPlayerManager;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mehegeo on 10/5/17.
 */

public class NemeStatsMainActivityViewModel {

    private GamingGroupsManager mGamingGroupsManager;
    private AccountManager mAccountManager;
    private List<GamingGroup> mGamingGroupList;
    private DisposableCompletableObserver mDataSyncDisposableObserver;
    private SyncManager mSyncManager;
    private UserPlayerManager mUserPlayerManager;
    private PlayersManager mPlayersManager;

    @Inject
    public NemeStatsMainActivityViewModel(PlayersManager playersManager, UserPlayerManager userPlayerManager, GamingGroupsManager gamingGroupsManager, AccountManager accountManager, SyncManager syncManager) {
        mPlayersManager = playersManager;
        mUserPlayerManager = userPlayerManager;
        mGamingGroupsManager = gamingGroupsManager;
        mAccountManager = accountManager;
        mSyncManager = syncManager;
    }

    public List<GamingGroup> getAllGamingGroups() {
        if (mGamingGroupList == null) {
            mGamingGroupList = mGamingGroupsManager.getAll(true);
        }
        return mGamingGroupList;
    }


    public void setSelectedGamingGroup(GamingGroup gamingGroup) {
        mAccountManager.setSelectedGamingGroup(gamingGroup);
    }

    public String getSelectedGamingGroupServerId() {
        return mAccountManager.getSelectedGamingGroupServerId();
    }

    public GamingGroup getSelectedGamingGroup() {
        String selectedGamingGroupServerId = getSelectedGamingGroupServerId();

        if (selectedGamingGroupServerId == null) {
            return getAllGamingGroups().get(0);
        }

        GamingGroup selectedGamingGroup = null;
        for (GamingGroup gamingGroup : getAllGamingGroups()) {
            if (gamingGroup.getServerId().equals(selectedGamingGroupServerId)) {
                selectedGamingGroup = gamingGroup;
                break;
            }
        }

        if (selectedGamingGroup == null) {
            return getAllGamingGroups().get(0);
        }

        return selectedGamingGroup;

    }

    public int getIndexOfSelectedGamingGroup() {
        return getAllGamingGroups().indexOf(getSelectedGamingGroup());
    }

    public void startDataSync() {
        mDataSyncDisposableObserver = new DisposableCompletableObserver() {
            @Override
            public void onComplete() {
//                if (mOnLoginListener != null) {
//                    mOnLoginListener.onInitialSyncSuccess();
//                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
//                performLogout();
//                if (mOnLoginListener != null) {
//                    mOnLoginListener.onInitialSyncFail();
//                }
            }
        };

        mSyncManager.triggerSyncData(false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mDataSyncDisposableObserver);

    }

    public String getAccountUserName() {
        return mAccountManager.getAccountUserName();
    }

    public void logout() {
        mAccountManager.deleteAllUserData();
    }

    public String getAvatarForCurrentUser() {
        String selectedGamingGroupUserId = mAccountManager.getSelectedGamingGroupServerId();
        if (selectedGamingGroupUserId != null) {
            UserPlayer userPlayer = mUserPlayerManager.getPlayerForGamingGroupId(selectedGamingGroupUserId);
            if (userPlayer != null) {
                Player player = mPlayersManager.getPlayerById(userPlayer.getPlayerServerId());
                if (player != null) {
                    return player.getRemoteAvatarUrl();
                }
            }
        }
        return null;
    }
}
