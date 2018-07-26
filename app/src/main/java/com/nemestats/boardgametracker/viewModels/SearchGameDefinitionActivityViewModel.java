package com.nemestats.boardgametracker.viewModels;


import android.content.Context;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.BoardGameGeekManager;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.viewModels.UIViewModel.SearchResultBoardGame;
import com.nemestats.boardgametracker.webservices.dto.SearchGameXMLDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionResponseDTO;
import com.nemestats.boardgametracker.webservices.errorHandling.GenericRXErrorHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by geomehedeniuc on 3/25/18.
 */

public class SearchGameDefinitionActivityViewModel {

    private GameDefinitionManager mGameDefinitionManager;
    private BoardGameGeekManager mBoardGameGeekManager;
    private AccountManager mAccountManager;

    private List<SearchResultBoardGame> mSearchResultBoardGameList;

    private SearchResultBoardGame mSelectedGame;
    private GameDefinition mSelectedGameDefinition;

    private DisposableObserver<GameDefinition> mDownloadGameThumbnailDisposableObserver;
    private OnGameDetailsListener mOnGameDetailsListener;
    private Context mContext;

    @Inject
    public SearchGameDefinitionActivityViewModel(Context context, BoardGameGeekManager boardGameGeekManager, GameDefinitionManager gameDefinitionManager, AccountManager accountManager) {
        mContext = context;
        mBoardGameGeekManager = boardGameGeekManager;
        mGameDefinitionManager = gameDefinitionManager;
        mAccountManager = accountManager;
        mSearchResultBoardGameList = new ArrayList<>();
    }

    public Completable searchForGames(String boardGameName) {
        mSearchResultBoardGameList.clear();

        return Completable.fromObservable(mBoardGameGeekManager.searchForGamesByName(boardGameName).map((Function<SearchGameXMLDTO, Object>) searchGameXMLDTO -> {
            for (SearchGameXMLDTO.BoardGameEntry entry : searchGameXMLDTO.getBoardGameList()) {
                if (entry.getObjectid() != null) {
                    SearchResultBoardGame searchResultBoardGame = new SearchResultBoardGame(entry.getObjectid(), entry.getName(), entry.getYearPublished());
                    mSearchResultBoardGameList.add(searchResultBoardGame);
                }
            }
            return searchGameXMLDTO;
        }));
    }

    public void setOnGameDetailsListener(OnGameDetailsListener onGameDetailsListener) {
        mOnGameDetailsListener = onGameDetailsListener;
    }

    public GameDefinition getSelectedGameDefinition() {
        return mSelectedGameDefinition;
    }

    public List<SearchResultBoardGame> getSearchResultList() {
        return mSearchResultBoardGameList;
    }

    public void setSelectedGame(SearchResultBoardGame searchResultBoardGame) {
        if (mSelectedGameDefinition != null && mSelectedGameDefinition.getServerId() == 0) {
            mGameDefinitionManager.deleteGameDefinition(mSelectedGameDefinition);
        }
        mSelectedGame = searchResultBoardGame;
    }

    public void requestGameDetails() {

        if (mDownloadGameThumbnailDisposableObserver != null && !mDownloadGameThumbnailDisposableObserver.isDisposed()) {
            mDownloadGameThumbnailDisposableObserver.dispose();
        }

        GameDefinition localGameDefinitionForCurrentlySelectedGamingGroup =
                mGameDefinitionManager.getGameDefinitionByBoardGameGeekIdAndGroupId(
                        Integer.parseInt(mSelectedGame.getBoardGameObjectId()),
                        mAccountManager.getSelectedGamingGroupServerId()
                );

        if (localGameDefinitionForCurrentlySelectedGamingGroup != null) {
            mSelectedGameDefinition = localGameDefinitionForCurrentlySelectedGamingGroup;
            if (mOnGameDetailsListener != null) {
                mOnGameDetailsListener.onGameDetailsRetrievedSuccess();
            }
            return;
        }

        if (mDownloadGameThumbnailDisposableObserver == null || mDownloadGameThumbnailDisposableObserver.isDisposed()) {
            mDownloadGameThumbnailDisposableObserver = new DisposableObserver<GameDefinition>() {
                @Override
                public void onNext(@NonNull GameDefinition gameDefinition) {
                    mSelectedGameDefinition = gameDefinition;
                    mGameDefinitionManager.save(Collections.singletonList(mSelectedGameDefinition));
                    if (mOnGameDetailsListener != null) {
                        mOnGameDetailsListener.onGameDetailsRetrievedSuccess();
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    if (mOnGameDetailsListener != null) {
                        mOnGameDetailsListener.onGameDetailsRetrievedFailed();
                    }
                }

                @Override
                public void onComplete() {

                }
            };
        } else {
            return;
        }

        GameDefinition gameDefinition = new GameDefinition();
        gameDefinition.setGamingGroupId(mAccountManager.getSelectedGamingGroupServerId());
        gameDefinition.setBoardGameGeekObjectId(Integer.parseInt(mSelectedGame.getBoardGameObjectId()));
        String gameDefinitionName;

        if (mSelectedGame.getYear() != null) {
            gameDefinitionName = String.format("%s (%s)", mSelectedGame.getName(), mSelectedGame.getYear());
        } else {
            gameDefinitionName = mSelectedGame.getName();
        }

        gameDefinition.setGameDefinitionName(gameDefinitionName);

        if (mOnGameDetailsListener != null) {
            mOnGameDetailsListener.onGameDetailsRetrieveStarted();
        }


        mBoardGameGeekManager
                .downloadGameDefinitionDetails(gameDefinition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> mDownloadGameThumbnailDisposableObserver = null)
                .subscribe(mDownloadGameThumbnailDisposableObserver);
    }



    public interface OnGameDetailsListener {

        void onGameDetailsRetrieveStarted();

        void onGameDetailsRetrievedSuccess();

        void onGameDetailsRetrievedFailed();
    }
}
