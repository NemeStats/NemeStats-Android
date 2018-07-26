package com.nemestats.boardgametracker.viewModels;

import android.content.Context;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.managers.AccountManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;
import com.nemestats.boardgametracker.managers.PlayersManager;
import com.nemestats.boardgametracker.webservices.dto.players.PlayerResponseDTO;
import com.nemestats.boardgametracker.webservices.errorHandling.GenericRXErrorHandling;


import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by geomehedeniuc on 5/13/18.
 */

public class CreatePlayerActivityViewModel {

    private PlayersManager mPlayersManager;
    private Player mPlayer;
    private Context mContext;
    private DisposableObserver<PlayerResponseDTO> mCreatePlayerDisposableObserver;
    private DisposableCompletableObserver mUpdatePlayerCompletableObserver;

    private OnCreatePlayerListener mOnCreatePlayerListener;

    private AccountManager mAccountManager;
    private PlayedGamesManager mPlayedGamesManager;
    private boolean mEditMode;

    @Inject
    public CreatePlayerActivityViewModel(Context context, AccountManager accountManager, PlayersManager playersManager, PlayedGamesManager playedGamesManager) {
        mPlayersManager = playersManager;
        mAccountManager = accountManager;
        mPlayedGamesManager = playedGamesManager;
        mContext = context;
    }


    public void setOnGameDetailsListener(OnCreatePlayerListener onCreatePlayerListener) {
        mOnCreatePlayerListener = onCreatePlayerListener;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }

    public void createPlayer(String playerName, String emailAddress) {
        mPlayer.setPlayerName(null);
        mPlayer.setEmailAddress(null);

        if (playerName == null || playerName.isEmpty()) {
            if (mOnCreatePlayerListener != null) {
                mOnCreatePlayerListener.onCreatePlayerFailed(mContext.getString(R.string.player_name_cannot_be_empty));
            }
            return;
        }

        mPlayer.setPlayerName(playerName);
        if (emailAddress != null && !emailAddress.isEmpty()) {
            mPlayer.setEmailAddress(emailAddress);
        }
        if (mCreatePlayerDisposableObserver == null || mCreatePlayerDisposableObserver.isDisposed()) {
            mCreatePlayerDisposableObserver = new DisposableObserver<PlayerResponseDTO>() {
                @Override
                public void onNext(@NonNull PlayerResponseDTO playerResponseDTO) {
                    mCreatePlayerDisposableObserver = null;

                    if (playerResponseDTO != null) {
                        mPlayer.setServerId(playerResponseDTO.getPlayerId());
                        mPlayer.setNemestatsUrl(playerResponseDTO.getNemeStatsUrl());
                        mPlayer.setRemoteAvatarUrl(playerResponseDTO.getRegisteredUserGravatarUrl());
                        mPlayersManager.savePlayers(Collections.singletonList(mPlayer));
                    }
                    mPlayedGamesManager.updatePlayerNameInPlayedGames(mPlayer);
                    if (mOnCreatePlayerListener != null) {
                        mOnCreatePlayerListener.onPlayerCreatedSuccess();
                    }
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    mCreatePlayerDisposableObserver.dispose();

                    if (mOnCreatePlayerListener != null) {
                        mOnCreatePlayerListener.onCreatePlayerFailed(GenericRXErrorHandling.extractErrorMessage(mContext, e));
                    }
                }

                @Override
                public void onComplete() {
                    mCreatePlayerDisposableObserver = null;
                }
            };
        }

        mPlayersManager.saveRemotePlayer(mPlayer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCreatePlayerDisposableObserver);
    }

    public void initializeNewPlayer() {
        mPlayer = new Player();
        mPlayer.setActive(true);
        mPlayer.setGamingGroupServerId(mAccountManager.getSelectedGamingGroupServerId());
    }

    public boolean isEditMode() {
        return mEditMode;
    }

    public void setEditMode(boolean editMode) {
        mEditMode = editMode;
    }

    public void updatePlayer(String playerName, boolean isActive) {
        mPlayer.setPlayerName(playerName);
        mPlayer.setActive(isActive);

        if (mUpdatePlayerCompletableObserver == null || mUpdatePlayerCompletableObserver.isDisposed()) {
            mUpdatePlayerCompletableObserver = new DisposableCompletableObserver() {
                @Override
                public void onError(@NonNull Throwable e) {
                    mUpdatePlayerCompletableObserver.dispose();
                    if (mOnCreatePlayerListener != null) {
                        mOnCreatePlayerListener.onUpdatePlayerFailed(GenericRXErrorHandling.extractErrorMessage(mContext, e));
                    }
                }

                @Override
                public void onComplete() {
                    mUpdatePlayerCompletableObserver = null;
                    mPlayersManager.savePlayers(Collections.singletonList(mPlayer));
                    mPlayedGamesManager.updatePlayerNameInPlayedGames(mPlayer);
                    if (mOnCreatePlayerListener != null) {
                        mOnCreatePlayerListener.onPlayerUpdatedSuccess();
                    }
                }
            };
        }

        mPlayersManager.updateRemotePlayer(mPlayer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mUpdatePlayerCompletableObserver);

    }

    public interface OnCreatePlayerListener {
        void onPlayerUpdatedSuccess();

        void onPlayerCreatedSuccess();

        void onUpdatePlayerFailed(String errorMessage);

        void onCreatePlayerFailed(String errorMessage);
    }
}
