package com.nemestats.boardgametracker.viewModels;

import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.managers.GamingGroupsManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;
import com.nemestats.boardgametracker.managers.PlayersManager;
import com.nemestats.boardgametracker.managers.SyncManager;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GamingGroupViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mehegeo on 10/5/17.
 */

public class GamingGroupFragmentViewModel {

    private PlayersManager mPlayersManager;
    private GameDefinitionManager mGameDefinitionManager;
    private PlayedGamesManager mPlayedGamesManager;
    private GamingGroupsManager mGamingGroupsManager;

    private List<GamingGroupViewModel> mGamingGroupViewModelList;
    private OnDataLoadedListener mOnDataLoadedListener;
    private SyncManager mSyncManager;

    @Inject
    public GamingGroupFragmentViewModel(PlayersManager playersManager,
                                        GameDefinitionManager gameDefinitionManager,
                                        PlayedGamesManager playedGamesManager,
                                        GamingGroupsManager gamingGroupsManager,
                                        SyncManager syncManager) {
        mPlayersManager = playersManager;
        mGameDefinitionManager = gameDefinitionManager;
        mPlayedGamesManager = playedGamesManager;
        mGamingGroupsManager = gamingGroupsManager;
        mSyncManager = syncManager;
    }

    public List<GamingGroupViewModel> getGamingGroupViewModelList() {
        if (mGamingGroupViewModelList == null) {
            mGamingGroupViewModelList = new ArrayList<>();
        }
        return mGamingGroupViewModelList;
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

    public void buildGamingGroupsViewModelList() {

        List<GamingGroupViewModel> gamingGroupViewModelList = new ArrayList<>();

        for (GamingGroup gamingGroup : mGamingGroupsManager.getAll(true)) {
            GamingGroupViewModel gamingGroupViewModel = new GamingGroupViewModel(gamingGroup);
            gamingGroupViewModel.setNumberOfGameDefinitionsInGamingGroup(getNumberOfGameDefinitionsInGamingGroup(gamingGroup));
            gamingGroupViewModel.setNumberOfPlayedGamesInGamingGroup(getNumberOfPlayedGamesInGamingGroup(gamingGroup));

            List<Player> playerList = mPlayersManager.getLocalPlayersInGamingGroup(gamingGroup.getServerId(), true);
            int index = 0;
            StringBuilder playersNames = new StringBuilder();
            for (Player player : playerList) {
                if (index > 3) {
                    break;
                }
                playersNames.append(player.getPlayerName());
                playersNames.append(", ");

                index++;
            }
            if (playerList.size() - index > 0) {
                playersNames.append("+");
                playersNames.append(playerList.size() - index);
            } else {
                playersNames.deleteCharAt(playersNames.lastIndexOf(","));
            }
            gamingGroupViewModel.setPlayersNames(playersNames.toString());

            gamingGroupViewModelList.add(gamingGroupViewModel);
        }

        mGamingGroupViewModelList.clear();
        mGamingGroupViewModelList.addAll(gamingGroupViewModelList);

        if (mOnDataLoadedListener != null) {
            mOnDataLoadedListener.onDataLoaded();
        }
    }

    public int getNumberOfGameDefinitionsInGamingGroup(GamingGroup gamingGroup) {
        return (int) mGameDefinitionManager.getLocalNumberOfGameDefinitionsInGamingGroup(gamingGroup.getServerId());
    }

    public int getNumberOfPlayersInGamingGroup(GamingGroup gamingGroup) {
        return (int) mPlayersManager.getLocalNumberOfPlayersInGamingGroup(gamingGroup.getServerId());
    }

    public int getNumberOfPlayedGamesInGamingGroup(GamingGroup gamingGroup) {
        return (int) mPlayedGamesManager.getLocalNumberOfPlayedGamesInGamingGroup(gamingGroup);
    }


    public void setOnDataLoadedListener(OnDataLoadedListener onDataLoadedListener) {
        mOnDataLoadedListener = onDataLoadedListener;
    }

    public interface OnDataLoadedListener {
        void onDataLoaded();
    }
}
