package com.nemestats.boardgametracker.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nemestats.boardgametracker.broadcasts.DataSyncBroadcastReceiver;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.Player;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by mehegeo on 10/5/17.
 */

public class SyncManager {

    private AccountManager mAccountManager;
    private GamingGroupsManager mGamingGroupsManager;
    private PlayersManager mPlayersManager;
    private PlayedGamesManager mPlayedGamesManager;
    private GameDefinitionManager mGameDefinitionManager;
    private Context mContext;

    public static final int ONE_MINUTE = 60 * 1000;
    public static final String TAG = SyncManager.class.getSimpleName();

    @Inject
    public SyncManager(AccountManager accountManager,
                       GamingGroupsManager gamingGroupsManager,
                       PlayersManager playersManager,
                       PlayedGamesManager playedGamesManager,
                       GameDefinitionManager gameDefinitionManager,
                       Context context) {
        mAccountManager = accountManager;
        mGamingGroupsManager = gamingGroupsManager;
        mPlayersManager = playersManager;
        mPlayedGamesManager = playedGamesManager;
        mGameDefinitionManager = gameDefinitionManager;
        mContext = context;
    }

    public Completable triggerSyncData(boolean forced) {
        if (forced) {
            return syncData();
        } else {
            if (mAccountManager.getLastSyncDate() < System.currentTimeMillis() - ONE_MINUTE) {
                return syncData();
            } else {
                return Completable.error(new Throwable("Sync Triggered too often"));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Completable syncData() {
        return Completable.fromObservable(
                getUserInformation()
                        .concatWith(getPlayersInUserGamingGroups())
                        .concatWith(getPlayedGamesInGamingGroups())
                        .concatWith(getGameDefinitionsInGamingGroups()))
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "accept:" + throwable.getStackTrace());
                        broadcastEvent(DataSyncBroadcastReceiver.DATA_SYNC_FINISHED_FAIL);
                    }
                })
                .doOnComplete(() -> {
                    mAccountManager.saveLastSyncDate(System.currentTimeMillis());
                    broadcastEvent(DataSyncBroadcastReceiver.DATA_SYNC_FINISHED_SUCCESS);
                });
    }


    public void broadcastEvent(String action) {
        Intent broadcastIntent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(broadcastIntent);
    }


    private Observable getUserInformation() {
        return mAccountManager.getUserInformation();
    }

    private Observable getPlayersInUserGamingGroups() {
        return Observable.fromCallable(() -> mGamingGroupsManager.getAll(false)).concatMap((Function<List<GamingGroup>, ObservableSource<?>>) gamingGroups -> Observable.fromIterable(gamingGroups)
                .concatMap(
                        gamingGroup -> mPlayersManager.getPlayersInGamingGroup(gamingGroup.getServerId())
                                .map(playerList -> {
                                    for (Player player : playerList) {
                                        mPlayedGamesManager.updatePlayerNameInPlayedGames(player);
                                    }
                                    return playerList;
                                }))
                .doOnNext(playerList -> mPlayersManager.savePlayers(playerList)));
    }

    public Observable getPlayedGamesInGamingGroups() {
        return Observable.fromCallable(() -> mGamingGroupsManager.getAll(false)).concatMap(gamingGroups -> Observable.fromIterable(gamingGroups)
                .concatMap(gamingGroup -> mPlayedGamesManager.getPlayedGamesForGamingGroup(gamingGroup.getServerId(), mAccountManager.getLastSyncDate())))
                .doOnNext(playedGamesList -> mPlayedGamesManager.save(playedGamesList));
    }

    public Observable getGameDefinitionsInGamingGroups() {
        return Observable.fromCallable(() -> mGamingGroupsManager.getAll(false)).concatMap(gamingGroups -> Observable.fromIterable(gamingGroups)
                .concatMap(
                        gamingGroup -> mGameDefinitionManager.getGameDefinitionsForGamingGroup(gamingGroup.getServerId())
                                .map(gameDefinitions -> {
                                    for (GameDefinition gameDefinition : gameDefinitions) {
                                        mPlayedGamesManager.updateGameDefinitionInPlayedGames(gameDefinition);
                                    }
                                    return gameDefinitions;
                                })))
                .doOnNext(gameDefinitionList -> mGameDefinitionManager.save(gameDefinitionList));
    }
}
