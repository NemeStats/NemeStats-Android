package com.nemestats.boardgametracker.viewModels;

import android.content.Context;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionResponseDTO;
import com.nemestats.boardgametracker.webservices.errorHandling.GenericRXErrorHandling;

import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by geomehedeniuc on 5/20/18.
 */

public class EditGameDefinitionActivityViewModel {

    private GameDefinitionManager mGameDefinitionManager;
    private GameDefinition mGameDefinition;

    private DisposableObserver<CreateGameDefinitionResponseDTO> mCreateGameDefinitionDisposableObserver;
    private DisposableCompletableObserver mUpdateGameDefinitionDisposableCompletableObserver;
    private Context mContext;
    private OnGameDetailsListener mOnGameDetailsListener;
    private boolean mIsInEditMode;

    PlayedGamesManager mPlayedGamesManager;

    @Inject
    public EditGameDefinitionActivityViewModel(Context context, GameDefinitionManager gameDefinitionManager, PlayedGamesManager playedGamesManager) {
        mContext = context;
        mGameDefinitionManager = gameDefinitionManager;
        mPlayedGamesManager = playedGamesManager;
    }

    public void setOnGameDetailsListener(OnGameDetailsListener onGameDetailsListener) {
        mOnGameDetailsListener = onGameDetailsListener;
    }

    public boolean isInEditMode() {
        return mIsInEditMode;
    }

    public void setInEditMode(boolean inEditMode) {
        mIsInEditMode = inEditMode;
    }

    public GameDefinition getGameDefinition() {
        return mGameDefinition;
    }

    public void setGameDefinition(GameDefinition gameDefinition) {
        mGameDefinition = gameDefinition;
    }

    private void updateGameDefinition() {
        if (mUpdateGameDefinitionDisposableCompletableObserver == null || mUpdateGameDefinitionDisposableCompletableObserver.isDisposed()) {
            mUpdateGameDefinitionDisposableCompletableObserver = new DisposableCompletableObserver() {
                @Override
                public void onComplete() {
                    mUpdateGameDefinitionDisposableCompletableObserver = null;
                    mGameDefinitionManager.save(Collections.singletonList(mGameDefinition));
                    mPlayedGamesManager.updateGameDefinitionInPlayedGames(mGameDefinition);

                    if (mOnGameDetailsListener != null) {
                        mOnGameDetailsListener.onCreateGameDefinitionSuccess();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    mUpdateGameDefinitionDisposableCompletableObserver.dispose();

                    if (mOnGameDetailsListener != null) {
                        mOnGameDetailsListener.onCreateGameDefinitionFailed(GenericRXErrorHandling.extractErrorMessage(mContext, e));
                    }
                }
            };
        }

        mGameDefinitionManager.updateRemoteGameDefinition(mGameDefinition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mUpdateGameDefinitionDisposableCompletableObserver);
    }

    private void createGameDefinition() {
        if (mCreateGameDefinitionDisposableObserver == null || mCreateGameDefinitionDisposableObserver.isDisposed()) {
            mCreateGameDefinitionDisposableObserver = new DisposableObserver<CreateGameDefinitionResponseDTO>() {
                @Override
                public void onNext(@NonNull CreateGameDefinitionResponseDTO createGameDefinitionResponseDTO) {
                    mCreateGameDefinitionDisposableObserver = null;

                    if (createGameDefinitionResponseDTO != null) {
                        mGameDefinition.setServerId(createGameDefinitionResponseDTO.getGameDefinitionId());
                        mGameDefinition.setActive(true);
                        mGameDefinition.setNemestatsUrl(createGameDefinitionResponseDTO.getNemeStatsUrl());
                        mGameDefinitionManager.save(Collections.singletonList(mGameDefinition));
                    }

                    if (mOnGameDetailsListener != null) {
                        mOnGameDetailsListener.onCreateGameDefinitionSuccess();
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    mCreateGameDefinitionDisposableObserver.dispose();

                    if (mOnGameDetailsListener != null) {
                        mOnGameDetailsListener.onCreateGameDefinitionFailed(GenericRXErrorHandling.extractErrorMessage(mContext, e));
                    }
                }

                @Override
                public void onComplete() {
                    mCreateGameDefinitionDisposableObserver = null;
                }
            };
        }

        mGameDefinitionManager.saveRemoteGameDefinition(mGameDefinition)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCreateGameDefinitionDisposableObserver);
    }


    public void saveGameDefinition() {
        if (isInEditMode()) {
            updateGameDefinition();
        } else {
            createGameDefinition();
        }
    }

    public void deleteSelectedGameDefinitionIfNoServerId() {
        if (mGameDefinition != null && mGameDefinition.getServerId() == 0) {
            mGameDefinitionManager.deleteGameDefinition(mGameDefinition);
        }
    }

    public interface OnGameDetailsListener {

        void onCreateGameDefinitionSuccess();

        void onCreateGameDefinitionFailed(String errorMessage);
    }
}
