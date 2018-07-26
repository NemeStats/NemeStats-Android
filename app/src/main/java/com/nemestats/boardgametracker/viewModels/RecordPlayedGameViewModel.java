package com.nemestats.boardgametracker.viewModels;


import android.content.Context;
import android.util.Log;

import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;
import com.nemestats.boardgametracker.managers.PlayersManager;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GameDefinitionViewModel;
import com.nemestats.boardgametracker.viewModels.UIViewModel.PlayerViewModel;
import com.nemestats.boardgametracker.webservices.dto.playedGames.CreateNewGameResultResponseDTO;
import com.nemestats.boardgametracker.webservices.errorHandling.GenericRXErrorHandling;

import org.joda.time.DateTime;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by geomehedeniuc on 3/13/18.
 */

public class RecordPlayedGameViewModel {

    private static final String TAG = RecordPlayedGameViewModel.class.getSimpleName();

    private static RecordPlayedGameViewModel mRecordPlayedGameViewModelInstance = null;

    private PlayedGame mPlayedGame;
    private GameDefinitionViewModel mSelectedGameDefinitionViewModel;

    private List<GameDefinitionViewModel> mGameDefinitionViewModelList;
    private List<GameDefinitionViewModel> mGameDefinitionViewModelListOriginalList;

    private List<PlayerViewModel> mSelectedPlayerViewModelList;

    private List<PlayerViewModel> mPlayersInGamingGroup;
    private List<PlayerViewModel> mPlayersInGamingGroupOriginalList;

    private OnSaveGameListener mOnSaveGameListener;

    private int mCOOPResult = -1;

    @Inject
    GameDefinitionManager mGameDefinitionManager;

    @Inject
    AccountManager mAccountManager;

    @Inject
    PlayedGamesManager mPlayedGamesManager;

    @Inject
    PlayersManager mPlayersManager;

    @Inject
    Context mContext;

    protected RecordPlayedGameViewModel() {
        NemeStatsApplication.getAppGraph().inject(this);
        buildGameDefinitionData();
        buildPlayersData();
    }

    public static RecordPlayedGameViewModel getInstance() {
        if (mRecordPlayedGameViewModelInstance == null) {
            mRecordPlayedGameViewModelInstance = new RecordPlayedGameViewModel();
        }
        return mRecordPlayedGameViewModelInstance;
    }

    public void destroyInstance() {
        mPlayedGame = null;
        mSelectedGameDefinitionViewModel = null;
        mGameDefinitionViewModelList = null;
        mGameDefinitionViewModelListOriginalList = null;
        mSelectedPlayerViewModelList = null;
        mPlayersInGamingGroup = null;
        mPlayersInGamingGroupOriginalList = null;
        mOnSaveGameListener = null;
        mGameDefinitionManager = null;
        mAccountManager = null;
        mPlayedGamesManager = null;
        mPlayersManager = null;
        mContext = null;
        mRecordPlayedGameViewModelInstance = null;
        System.gc();
    }

    public void setOnSaveGameListener(OnSaveGameListener onSaveGameListener) {
        mOnSaveGameListener = onSaveGameListener;
    }

    public boolean isToday() {
        DateTime today = DateTime.now();
        return (mPlayedGame.getDatePlayed().getYear() == today.getYear())
                && (mPlayedGame.getDatePlayed().getDayOfYear() == today.getDayOfYear());
    }

    public boolean isYesterday() {
        DateTime yesterday = DateTime.now().minusDays(1);
        return (mPlayedGame.getDatePlayed().getYear() == yesterday.getYear())
                && (mPlayedGame.getDatePlayed().getDayOfYear() == yesterday.getDayOfYear());
    }

    public GameDefinitionViewModel getSelectedGameDefinitionViewModel() {
        return mSelectedGameDefinitionViewModel;
    }

    public int getCOOPResult() {
        return mCOOPResult;
    }

    public void setCOOPResult(int COOPResult) {
        mCOOPResult = COOPResult;
    }

    public void setSelectedGameDefinitionViewModel(GameDefinitionViewModel selectedGameDefinitionViewModel) {
        mSelectedGameDefinitionViewModel = selectedGameDefinitionViewModel;

        for (GameDefinitionViewModel gameDefinitionViewModel : mGameDefinitionViewModelListOriginalList) {
            if (gameDefinitionViewModel.getGameDefinition().getServerId() == mSelectedGameDefinitionViewModel.getGameDefinition().getServerId()) {
                gameDefinitionViewModel.setSelected(true);
            } else {
                gameDefinitionViewModel.setSelected(false);
            }
        }
        mPlayedGame.setGameDefinitionName(selectedGameDefinitionViewModel.getGameDefinition().getGameDefinitionName());
        mPlayedGame.setGameDefinitionId(selectedGameDefinitionViewModel.getGameDefinition().getServerId());
        mPlayedGame.setBoardGameGeekObjectId(selectedGameDefinitionViewModel.getGameDefinition().getBoardGameGeekObjectId());
    }

    public void setDatePlayed(DateTime datePlayed) {
        mPlayedGame.setDatePlayed(datePlayed);
    }

    public void initializeNewGame() {
        mPlayedGame = new PlayedGame();
        mPlayedGame.setGameResultType(PlayedGameUtils.RESULT_TYPE_RANKED);
        mPlayedGame.setGamingGroupId(mAccountManager.getSelectedGamingGroupServerId());
        mPlayedGame.setDatePlayed(DateTime.now());
    }


    public PlayedGame getPlayedGame() {
        return mPlayedGame;
    }

    public List<GameDefinitionViewModel> getGameDefinitionListSortedByPlayedGames() {
        return mGameDefinitionViewModelList;
    }

    private void buildPlayersData() {
        mPlayersInGamingGroup = new ArrayList<>();
        mPlayersInGamingGroupOriginalList = new ArrayList<>();

        String selectedGamingGroupServerId = mAccountManager.getSelectedGamingGroupServerId();
        List<Player> playerList = mPlayersManager.getLocalPlayersInGamingGroup(selectedGamingGroupServerId, true);
        for (Player player : playerList) {
            PlayerViewModel playerViewModel = new PlayerViewModel(player);
            mPlayersInGamingGroup.add(playerViewModel);
        }
        mPlayersInGamingGroupOriginalList.addAll(mPlayersInGamingGroup);
    }


    private void buildGameDefinitionData() {
        mGameDefinitionViewModelList = new ArrayList<>();
        mGameDefinitionViewModelListOriginalList = new ArrayList<>();
        String selectedGamingGroupServerId = mAccountManager.getSelectedGamingGroupServerId();
        List<GameDefinition> gameDefinitionList = mGameDefinitionManager.getLocalGameDefinitionsInGamingGroup(selectedGamingGroupServerId, true);

        for (GameDefinition gameDefinition : gameDefinitionList) {
            GameDefinitionViewModel gameDefinitionViewModel = new GameDefinitionViewModel(gameDefinition);
            gameDefinitionViewModel.setNumberOfPlayedGames(mPlayedGamesManager.getLocalNumberOfPlayedGamesInGamingGroupForGameDefinition(selectedGamingGroupServerId, gameDefinition.getServerId()));
            mGameDefinitionViewModelList.add(gameDefinitionViewModel);
        }
        mGameDefinitionViewModelListOriginalList.addAll(mGameDefinitionViewModelList);
    }

    public Completable searchLocalGames(String name) {
        return Completable.fromObservable(Observable.create(e -> {
            mGameDefinitionViewModelList.clear();
            for (GameDefinitionViewModel gameDefinitionViewModel : mGameDefinitionViewModelListOriginalList) {
                if (gameDefinitionViewModel.getGameDefinition().getGameDefinitionName().toLowerCase().contains(name.toLowerCase())) {
                    mGameDefinitionViewModelList.add(gameDefinitionViewModel);
                }
            }
            e.onComplete();
        }));
    }

    public List<PlayerViewModel> getPlayersInGamingGroup() {
        return mPlayersInGamingGroup;
    }

    public List<PlayerViewModel> getSelectedPlayers() {
        if (mSelectedPlayerViewModelList == null) {
            mSelectedPlayerViewModelList = Collections.synchronizedList(new ArrayList<PlayerViewModel>());
        }
        return mSelectedPlayerViewModelList;
    }

    public synchronized void onPlayerClick(PlayerViewModel playerViewModel) {
        boolean playerSelected = !playerViewModel.isSelected();

        if (playerSelected) {
            getSelectedPlayers().add(playerViewModel);
        } else {
            for (PlayerViewModel player : getSelectedPlayers()) {
                if (player.getPlayer().getId() == playerViewModel.getPlayer().getId()) {
                    mSelectedPlayerViewModelList.remove(player);
                    break;
                }
            }
        }
        playerViewModel.setSelected(playerSelected);
    }

    public Completable searchLocalPlayers(String s) {
        return Completable.fromObservable(Observable.create(e -> {
            mPlayersInGamingGroup.clear();
            for (PlayerViewModel playerViewModel : mPlayersInGamingGroupOriginalList) {
                if (playerViewModel.getPlayer().getPlayerName().toLowerCase().contains(s.toLowerCase())) {
                    mPlayersInGamingGroup.add(playerViewModel);
                }
            }
            e.onComplete();
        }));
    }

    public void addCreatedGameDefiniton(GameDefinitionViewModel extraGameDefinition) {
        mGameDefinitionViewModelList.add(extraGameDefinition);
        mGameDefinitionViewModelListOriginalList.add(extraGameDefinition);
    }

    public int getPlayedGameInsertedDetailsProgress() {

        int progress = 0;

        if (mPlayedGame.getDatePlayed() != null) {
            progress += 25;
        }

        if (mSelectedGameDefinitionViewModel != null) {
            progress += 25;
        }

        if (mSelectedPlayerViewModelList != null && mSelectedPlayerViewModelList.size() >= 2) {
            progress += 25;
        } else {
            return progress;
        }

        boolean setResultsCompleted = true;
        switch (mPlayedGame.getGameResultType()) {
            case PlayedGameUtils.RESULT_TYPE_RANKED:
                for (PlayerViewModel playerViewModel : mSelectedPlayerViewModelList) {
                    if (!playerViewModel.isRankAssigned()) {
                        setResultsCompleted = false;
                    }
                }
                break;
            case PlayedGameUtils.RESULT_TYPE_SCORED:
                for (PlayerViewModel playerViewModel : mSelectedPlayerViewModelList) {
                    if (playerViewModel.getPointsScored() == -1f) {
                        setResultsCompleted = false;
                    }
                }
                break;
            case PlayedGameUtils.RESULT_TYPE_CO_OP:
                break;
        }

        if (setResultsCompleted) {
            progress += 25;
        }
        return progress;
    }

    public void saveGame() {
        if (mOnSaveGameListener != null) {
            mOnSaveGameListener.onSaveGameStarted();
        }

        initPlayerGameResults();

        mPlayedGamesManager.saveRemoteGame(mPlayedGame)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<CreateNewGameResultResponseDTO>() {
                    @Override
                    public void onNext(CreateNewGameResultResponseDTO createNewGameResultResponseDTO) {
                        mPlayedGame.setServerId(createNewGameResultResponseDTO.getPlayedGameId());
                        mPlayedGame.setNemestatsUrl(createNewGameResultResponseDTO.getNemeStatsUrl());
                        mPlayedGamesManager.save(Collections.singletonList(mPlayedGame));

                        if (mOnSaveGameListener != null) {
                            mOnSaveGameListener.onSaveGameSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        String errorMessage = GenericRXErrorHandling.extractErrorMessage(mContext, e);
                        if (mOnSaveGameListener != null) {
                            mOnSaveGameListener.onSaveGameFailed(errorMessage);
                        }
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete: ");
                    }
                });
    }

    public void initPlayerGameResults() {

        mPlayedGame.getPlayerGameResultsList().clear();

        switch (mPlayedGame.getGameResultType()) {
            case PlayedGameUtils.RESULT_TYPE_RANKED:
                setupRankedGameResults();
                break;
            case PlayedGameUtils.RESULT_TYPE_SCORED:
                setupScoredGameResults();
                break;
            case PlayedGameUtils.RESULT_TYPE_CO_OP:
                setupCOOPGameResults();
                break;
        }
    }


    private void setupScoredGameResults() {

        List<PlayerViewModel> listCopy = new ArrayList<>(mSelectedPlayerViewModelList);
        Collections.copy(listCopy, mSelectedPlayerViewModelList);
        Collections.sort(listCopy, (lhs, rhs) -> lhs.getPointsScored() > rhs.getPointsScored() ? -1 : (lhs.getPointsScored() < rhs.getPointsScored()) ? 1 : 0);

        int rankList[] = new int[mSelectedPlayerViewModelList.size()];
        int currentRank = 0;
        float currentScoredPoints = Float.MAX_VALUE;

        for (int i = 0; i < listCopy.size(); i++) {
            if (listCopy.get(i).getPointsScored() < currentScoredPoints) {
                currentScoredPoints = listCopy.get(i).getPointsScored();
                currentRank++;
            }
            rankList[i] = currentRank;
        }

        for (PlayerViewModel playerViewModel : listCopy) {
            float scoredPoints = playerViewModel.getPointsScored();
            int playerServerId = playerViewModel.getPlayer().getServerId();
            PlayerGameResults playerGameResults = new PlayerGameResults();
            playerGameResults.setPlayerId(playerServerId);
            playerGameResults.setPointsScored(scoredPoints);
            playerGameResults.setGameRank(rankList[listCopy.indexOf(playerViewModel)]);
            playerGameResults.setPlayerName(playerViewModel.getPlayer().getPlayerName());
            playerGameResults.setPlayerActive(playerViewModel.getPlayer().isActive());
            playerGameResults.setPlayedGame(mPlayedGame);
            mPlayedGame.getPlayerGameResultsList().add(playerGameResults);
        }
    }

    private void setupRankedGameResults() {

        List<PlayerViewModel> listCopy = new ArrayList<>(mSelectedPlayerViewModelList);
        Collections.copy(listCopy, mSelectedPlayerViewModelList);
        Collections.sort(listCopy, (lhs, rhs) -> lhs.getAssignedRank() > rhs.getAssignedRank() ? 1 : (lhs.getAssignedRank() < rhs.getAssignedRank()) ? -1 : 0);

        for (PlayerViewModel playerViewModel : listCopy) {
            int assignedRank = playerViewModel.getAssignedRank();
            assignedRank++;
            int playerServerId = playerViewModel.getPlayer().getServerId();

            PlayerGameResults playerGameResults = new PlayerGameResults();
            playerGameResults.setPlayerId(playerServerId);
            playerGameResults.setGameRank(assignedRank);
            playerGameResults.setPlayerName(playerViewModel.getPlayer().getPlayerName());
            playerGameResults.setPlayerActive(playerViewModel.getPlayer().isActive());
            playerGameResults.setPlayedGame(mPlayedGame);

            mPlayedGame.getPlayerGameResultsList().add(playerGameResults);
        }
    }

    private void setupCOOPGameResults() {

        int rank = 2;
        if (mCOOPResult == PlayedGameUtils.TEAM_WIN_RANK) {
            rank = 1;
        }
        for (PlayerViewModel playerViewModel : mSelectedPlayerViewModelList) {
            int playerServerId = playerViewModel.getPlayer().getServerId();
            PlayerGameResults playerGameResults = new PlayerGameResults();
            playerGameResults.setPlayerId(playerServerId);
            playerGameResults.setGameRank(rank);
            playerGameResults.setPlayerName(playerViewModel.getPlayer().getPlayerName());
            playerGameResults.setPlayerActive(playerViewModel.getPlayer().isActive());
            playerGameResults.setPlayedGame(mPlayedGame);

            mPlayedGame.getPlayerGameResultsList().add(playerGameResults);
        }
    }


    public void setGameResultType(String gameResultType) {
        mPlayedGame.setGameResultType(gameResultType);
    }

    public int getIndexOfSelectedGame() {
        for (GameDefinitionViewModel gameDefinitionViewModel : mGameDefinitionViewModelList) {
            if (gameDefinitionViewModel.getGameDefinition().getServerId() == mSelectedGameDefinitionViewModel.getGameDefinition().getServerId()) {
                return mGameDefinitionViewModelList.indexOf(gameDefinitionViewModel);
            }
        }
        return -1;
    }

    public PlayerViewModel addCreatedPlayer(Player player) {
        if (player != null) {
            PlayerViewModel playerViewModel = new PlayerViewModel(player);
            mPlayersInGamingGroup.add(binarySearch(player, mPlayersInGamingGroup), playerViewModel);
            mPlayersInGamingGroupOriginalList.add(binarySearch(player, mPlayersInGamingGroupOriginalList), playerViewModel);
            return playerViewModel;
        }
        return null;
    }

    private int binarySearch(Player player, List<PlayerViewModel> list) {
        int low = 0;
        int high = list.size() - 1;
        while (high >= low) {
            int middle = (low + high) / 2;
            if (list.get(middle).getPlayer().getPlayerName().compareToIgnoreCase(player.getPlayerName()) > 1) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }
        return low;
    }

    public String getMissingInformation() {
        String missingIformation = mContext.getString(R.string.please_complete_the_following_steps);

        if (mPlayedGame.getDatePlayed() == null) {
            missingIformation += mContext.getString(R.string.choose_a_date);
        }

        if (mSelectedGameDefinitionViewModel == null) {
            missingIformation += mContext.getString(R.string.choose_a_game);
        }

        if (mSelectedPlayerViewModelList != null && mSelectedPlayerViewModelList.size() < 2) {
            missingIformation += mContext.getString(R.string.select_at_least_2_players);
        }

        boolean setResultsCompleted = true;
        switch (mPlayedGame.getGameResultType()) {
            case PlayedGameUtils.RESULT_TYPE_RANKED:
                for (PlayerViewModel playerViewModel : mSelectedPlayerViewModelList) {
                    if (!playerViewModel.isRankAssigned()) {
                        setResultsCompleted = false;
                    }
                }
                break;
            case PlayedGameUtils.RESULT_TYPE_SCORED:
                for (PlayerViewModel playerViewModel : mSelectedPlayerViewModelList) {
                    if (playerViewModel.getPointsScored() == -1f) {
                        setResultsCompleted = false;
                    }
                }
                break;
            case PlayedGameUtils.RESULT_TYPE_CO_OP:
                if (!mSelectedPlayerViewModelList.isEmpty()) {
                    int rank = mSelectedPlayerViewModelList.get(0).getAssignedRank();

                    for (PlayerViewModel playerViewModel : mSelectedPlayerViewModelList) {
                        if (playerViewModel.getAssignedRank() != rank)
                            setResultsCompleted = false;
                    }
                }
                break;
        }

        if (!setResultsCompleted || mSelectedPlayerViewModelList == null || mSelectedPlayerViewModelList.isEmpty()) {
            missingIformation += mContext.getString(R.string.set_results);
        }
        return missingIformation;
    }


    public interface OnSaveGameListener {
        void onSaveGameStarted();

        void onSaveGameSuccess();

        void onSaveGameFailed(String errorMessage);
    }
}
