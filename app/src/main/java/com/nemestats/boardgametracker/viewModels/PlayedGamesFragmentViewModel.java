package com.nemestats.boardgametracker.viewModels;


import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.nemestats.boardgametracker.domain.UserPlayer;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.managers.GamingGroupsManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;
import com.nemestats.boardgametracker.managers.SyncManager;
import com.nemestats.boardgametracker.managers.UserPlayerManager;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayedGameHeaderData;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayedGameViewModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mehegeo on 10/12/17.
 */

public class PlayedGamesFragmentViewModel {

    private PlayedGamesManager mPlayedGamesManager;
    private GamingGroupsManager mGamingGroupsManager;
    private List<PlayedGame> mPlayedGameList;
    private List<PlayedGameViewModel> mPlayedGameViewModelsList;
    private AccountManager mAccountManager;
    private GameDefinitionManager mGameDefinitionManager;
    private HashMap<Integer, PlayedGameHeaderData> mHeadersPositions;
    private UserPlayerManager mUserPlayerManager;
    private OnDataLoadedListener mOnDataLoadedListener;
    private int mIndexToBeInserted;
    public static final int MAX_RECORDS_DISPAYED = 20;
    private SyncManager mSyncManager;

    @Inject
    public PlayedGamesFragmentViewModel(GameDefinitionManager gameDefinitionManager,
                                        AccountManager accountManager,
                                        PlayedGamesManager playedGamesManager,
                                        GamingGroupsManager gamingGroupsManager,
                                        UserPlayerManager userPlayerManager,
                                        SyncManager syncManager) {
        mGameDefinitionManager = gameDefinitionManager;
        mAccountManager = accountManager;
        mPlayedGamesManager = playedGamesManager;
        mGamingGroupsManager = gamingGroupsManager;
        mUserPlayerManager = userPlayerManager;
        mPlayedGameList = new ArrayList<>();
        mPlayedGameViewModelsList = new ArrayList<>();
        mHeadersPositions = new HashMap<>();
        mSyncManager = syncManager;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener onDataLoadedListener) {
        mOnDataLoadedListener = onDataLoadedListener;
    }

    public List<PlayedGameViewModel> getPlayedGamesViewModelList() {
        return mPlayedGameViewModelsList;
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

    public void buildPlayedGamesData(boolean refreshFromDatabase) {
        List<PlayedGameViewModel> playedGameViewModelList = new ArrayList<>();
        HashMap<Integer, PlayedGameHeaderData> headersPosition = new HashMap<>();

        List<PlayedGame> playedGameList;

        if (refreshFromDatabase) {
            playedGameList = getSortedPlayedGamesList();
        } else {
            playedGameList = mPlayedGameList;
        }

        UserPlayer userPlayer = mUserPlayerManager.getPlayerForGamingGroupId(getSelectedGamingGroupServerId());
        DateTime currentDate = null;


        int listSize = playedGameList.size();

        int lastInsertedItem = 0;

        PlayedGameHeaderData playedGameHeaderData = new PlayedGameHeaderData();

        for (int i = 0; i < listSize; i++) {
            if (currentDate == null) {
                currentDate = playedGameList.get(i).getDatePlayed();
            }


            playedGameHeaderData.setHeaderDate(currentDate);

            if (currentDate.getYear() != playedGameList.get(i).getDatePlayed().getYear() ||
                    currentDate.getDayOfYear() != playedGameList.get(i).getDatePlayed().getDayOfYear()) {
                lastInsertedItem = i;
                currentDate = playedGameList.get(i).getDatePlayed();
                playedGameHeaderData = new PlayedGameHeaderData();
            }

            playedGameHeaderData.setHeaderPosition(lastInsertedItem);


            PlayedGameViewModel playedGameViewModel = new PlayedGameViewModel(playedGameList.get(i));
            playedGameViewModel.setGameDefinition(mGameDefinitionManager.getGameDefinitionByServerId(playedGameList.get(i).getGameDefinitionId()));
            if (playedGameViewModel.getGameDefinition() != null) {
                playedGameViewModel.getGameDefinition().setDescription(null);
            }
            List<Integer> gameResultsList = new ArrayList<>();
            Integer pointsScoredByUser = Integer.valueOf(0);
            Collections.sort(playedGameList.get(i).getPlayerGameResultsList(), (o1, o2) -> {
                if (o1.getGameRank() == o2.getGameRank()) {
                    return o1.getPlayerName().compareToIgnoreCase(o2.getPlayerName());
                }
                if (o1.getGameRank() > o2.getGameRank()) {
                    return 1;
                } else {
                    return -1;
                }
            });

            if (userPlayer != null) {
                for (PlayerGameResults playerGameResults : playedGameList.get(i).getPlayerGameResultsList()) {

                    gameResultsList.add(playerGameResults.getTotalNemeStatsPointsAwarded());

                    if (playerGameResults.getPlayerId() == userPlayer.getPlayerServerId()) {
                        pointsScoredByUser = playerGameResults.getTotalNemeStatsPointsAwarded();
                        playedGameHeaderData.setTotalNemeStatsPoints(playedGameHeaderData.getTotalNemeStatsPoints() + pointsScoredByUser);
                        playedGameViewModel.setEarnedPoints(playerGameResults.getTotalNemeStatsPointsAwarded());
                    }
                }
            }
            Collections.sort(gameResultsList, (o1, o2) -> -o1.compareTo(o2));
            playedGameViewModel.setAwardedPosition(gameResultsList.indexOf(pointsScoredByUser) + 1);

            headersPosition.put(i, playedGameHeaderData);

            playedGameViewModelList.add(playedGameViewModel);
        }

        mPlayedGameViewModelsList.clear();
        mHeadersPositions.clear();

        mPlayedGameViewModelsList.addAll(playedGameViewModelList);
        mHeadersPositions.putAll(headersPosition);

        if (mOnDataLoadedListener != null) {
            mOnDataLoadedListener.onDataLoaded(!refreshFromDatabase, mIndexToBeInserted);
        }
    }

    public String getSelectedGamingGroupServerId() {
        return mAccountManager.getSelectedGamingGroupServerId();
    }

    public List<PlayedGame> getSortedPlayedGamesList() {
        mPlayedGameList = mPlayedGamesManager.getSortedPlayedGamesList(getSelectedGamingGroupServerId(), MAX_RECORDS_DISPAYED);
        return mPlayedGameList;
    }

    public HashMap<Integer, PlayedGameHeaderData> getHeadersPosition() {
        return mHeadersPositions;
    }

    public void onGameDefinitionUpdated(GameDefinition gameDefinition) {
        for (PlayedGameViewModel playedGameViewModel : mPlayedGameViewModelsList) {
            if (playedGameViewModel.getGameDefinition() == null) {
                continue;
            }
            if (playedGameViewModel.getGameDefinition().getServerId() == gameDefinition.getServerId()) {
                playedGameViewModel.getGameDefinition().setThumbnailLocalPath(gameDefinition.getThumbnailLocalPath());
            }
        }
        if (mOnDataLoadedListener != null) {
            mOnDataLoadedListener.onDataLoaded(false, mIndexToBeInserted);
        }
    }

    /**
     * Inserts a Played game sorted in the played games list
     *
     * @param playedGame - Played Game to be added sorted
     */
    public void onPlayedGameCreated(PlayedGame playedGame) {
        if (playedGame != null) {
            mIndexToBeInserted = binarySearch(playedGame);
            mPlayedGameList.add(mIndexToBeInserted, playedGame);
        }
    }

    private int binarySearch(PlayedGame playedGame) {
        int low = 0;
        int high = mPlayedGameList.size() - 1;
        while (high >= low) {
            int middle = (low + high) / 2;
            if (mPlayedGameList.get(middle).getDatePlayed().getMillis() < playedGame.getDatePlayed().getMillis()) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return low;
    }


    public interface OnDataLoadedListener {
        void onDataLoaded(boolean smoothScrollToPosition, int position);
    }
}
