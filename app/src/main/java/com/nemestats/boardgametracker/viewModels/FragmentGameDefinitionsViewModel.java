package com.nemestats.boardgametracker.viewModels;

import android.util.Log;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.BoardGameGeekManager;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;
import com.nemestats.boardgametracker.managers.SyncManager;
import com.nemestats.boardgametracker.viewModels.UIViewModel.GameDefinitionViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mehegeo on 10/8/17.
 */

public class FragmentGameDefinitionsViewModel {

    private static final String TAG = FragmentGameDefinitionsViewModel.class.getSimpleName();
    private AccountManager mAccountManager;
    private GameDefinitionManager mGameDefinitionManager;
    private List<GameDefinition> mGameDefinitionsList;
    private List<GameDefinitionViewModel> mGameDefinitionViewModelList;
    private PlayedGamesManager mPlayedGamesManager;
    private BoardGameGeekManager mBoardGameGeekManager;
    private DisposableObserver<GameDefinitionViewModel> mDownloadGameThumbnailDisposableObserver;
    private OnDataLoadedListener mOnDataLoadedListener;
    private SyncManager mSyncManager;

    @Inject
    public FragmentGameDefinitionsViewModel(SyncManager syncManager, PlayedGamesManager playedGamesManager, AccountManager accountManager, GameDefinitionManager gameDefinitionManager, BoardGameGeekManager boardGameGeekManager) {
        mSyncManager = syncManager;
        mAccountManager = accountManager;
        mGameDefinitionManager = gameDefinitionManager;
        mPlayedGamesManager = playedGamesManager;
        mGameDefinitionViewModelList = new ArrayList<>();
        mGameDefinitionsList = new ArrayList<>();
        mBoardGameGeekManager = boardGameGeekManager;
    }

    public List<GameDefinition> getGameDefinitionsList(boolean activeOnly) {
        mGameDefinitionsList = mGameDefinitionManager.getLocalGameDefinitionsInGamingGroup(getSelectedGamingGroupServerId(), activeOnly);
        return mGameDefinitionsList;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener onDataLoadedListener) {
        mOnDataLoadedListener = onDataLoadedListener;
    }

    public List<GameDefinitionViewModel> getGameDefinitionViewModelList() {
        return mGameDefinitionViewModelList;
    }

    public void buildGameDefinitionData() {
        List<GameDefinitionViewModel> gameDefinitionViewModelList = new ArrayList<>();
        mGameDefinitionViewModelList.clear();
        String selectedGamingGroupServerId = getSelectedGamingGroupServerId();
        for (GameDefinition gameDefinition : getGameDefinitionsList(true)) {
            GameDefinitionViewModel gameDefinitionViewModel = new GameDefinitionViewModel(gameDefinition);
            gameDefinitionViewModel.setNumberOfPlayedGames(mPlayedGamesManager.getLocalNumberOfPlayedGamesInGamingGroupForGameDefinition(selectedGamingGroupServerId, gameDefinition.getServerId()));
            gameDefinitionViewModelList.add(gameDefinitionViewModel);
        }
        mGameDefinitionViewModelList.clear();
        mGameDefinitionViewModelList.addAll(gameDefinitionViewModelList);

        if (mOnDataLoadedListener != null) {
            mOnDataLoadedListener.onDataLoaded();
        }
    }

    public void disposeObservables() {
        if (mDownloadGameThumbnailDisposableObserver != null && !mDownloadGameThumbnailDisposableObserver.isDisposed()) {
            mDownloadGameThumbnailDisposableObserver.dispose();
        }
        mDownloadGameThumbnailDisposableObserver = null;
    }

    public void downloadRequiredThumbnailsForGameDefinitions() {
        if (mDownloadGameThumbnailDisposableObserver == null) {
            mDownloadGameThumbnailDisposableObserver = new DisposableObserver<GameDefinitionViewModel>() {
                @Override
                public void onNext(@NonNull GameDefinitionViewModel gameDefinitionViewModel) {
                    if (mOnDataLoadedListener != null) {
                        mOnDataLoadedListener.onThumbnailDownloaded(gameDefinitionViewModel.getGameDefinition());
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {

                }
            };

            Observable.fromIterable(getGameDefinitionsWithoutValidThumbnail())
                    .concatMap(gameDefinitionViewModel ->
                            mBoardGameGeekManager.downloadGameDefinitionDetails(gameDefinitionViewModel.getGameDefinition())
                                    .subscribeOn(Schedulers.io())
                                    .map(gameDefinition -> {
                                        gameDefinitionViewModel.setGameDefinition(gameDefinition);
                                        mGameDefinitionManager.save(Collections.singletonList(gameDefinitionViewModel.getGameDefinition()));
                                        return gameDefinitionViewModel;
                                    })).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnComplete(() -> mDownloadGameThumbnailDisposableObserver = null)
                    .subscribe(mDownloadGameThumbnailDisposableObserver);
        }
    }

    public String getSelectedGamingGroupServerId() {
        return mAccountManager.getSelectedGamingGroupServerId();
    }

    public List<GameDefinitionViewModel> getGameDefinitionsWithoutValidThumbnail() {
        List<GameDefinitionViewModel> gameDefinitionViewModelList = new ArrayList<>();

        for (GameDefinitionViewModel gameDefinitionViewModel : getGameDefinitionViewModelList()) {
            if (gameDefinitionViewModel.getGameDefinition().getThumbnailLocalPath() == null || !new File(gameDefinitionViewModel.getGameDefinition().getThumbnailLocalPath()).exists()) {
                if (gameDefinitionViewModel.getGameDefinition().getDescription() == null || gameDefinitionViewModel.getGameDefinition().getDescription().isEmpty()) {
                    if (gameDefinitionViewModel.getGameDefinition().getBoardGameGeekObjectId() != 0) {
                        gameDefinitionViewModelList.add(gameDefinitionViewModel);
                    }
                }
            }
        }
        Log.e(TAG, "getGameDefinitionsWithoutValidThumbnail: " + gameDefinitionViewModelList.size());
        return gameDefinitionViewModelList;
    }

    public void addCreatedGameDefiniton(GameDefinition extraGameDefinition) {
        mGameDefinitionsList.add(extraGameDefinition);
        GameDefinitionViewModel gameDefinitionViewModel = new GameDefinitionViewModel(extraGameDefinition);
        gameDefinitionViewModel.setNumberOfPlayedGames(mPlayedGamesManager.getLocalNumberOfPlayedGamesInGamingGroupForGameDefinition(getSelectedGamingGroupServerId(), extraGameDefinition.getServerId()));
        mGameDefinitionViewModelList.add(0, gameDefinitionViewModel);
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

    public interface OnDataLoadedListener {
        void onDataLoaded();

        void onThumbnailDownloaded(GameDefinition gameDefinition);
    }
}
