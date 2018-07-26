package com.nemestats.boardgametracker.managers;

import com.nemestats.boardgametracker.dal.local.GameDefinitionLocalRepo;
import com.nemestats.boardgametracker.dal.remote.GameDefinitionsRemoteRepo;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.webservices.dto.players.UpdatePlayerRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionResponseDTO;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/7/17.
 */

public class GameDefinitionManager {


    private GameDefinitionsRemoteRepo mGameDefinitionsRemoteRepo;
    private GameDefinitionLocalRepo mGameDefinitionLocalRepo;
    private AccountManager mAccountManager;

    @Inject
    public GameDefinitionManager(GameDefinitionsRemoteRepo gameDefinitionsRemoteRepo, GameDefinitionLocalRepo gameDefinitionLocalRepo, AccountManager accountManager) {
        mGameDefinitionsRemoteRepo = gameDefinitionsRemoteRepo;
        mGameDefinitionLocalRepo = gameDefinitionLocalRepo;
        mAccountManager = accountManager;
    }

    public Observable<List<GameDefinition>> getGameDefinitionsForGamingGroup(String gamingGroupId) {
        return mGameDefinitionsRemoteRepo.getGameDefinitionsForGamingGroup(gamingGroupId).retryWhen(mAccountManager.getRefreshUserSessionFunction());
    }

    public void save(List<GameDefinition> gameDefinitionList) {
        mGameDefinitionLocalRepo.savePlayedGamesList(gameDefinitionList);
    }

    public List<GameDefinition> getLocalGameDefinitionsInGamingGroup(String serverId, boolean activeOnly) {
        return mGameDefinitionLocalRepo.getGameDefinitionsInGamingGroup(serverId,activeOnly);
    }

    public long getLocalNumberOfGameDefinitionsInGamingGroup(String serverId) {
        return mGameDefinitionLocalRepo.getLocalNumberOfGameDefinitionsInGamingGroup(serverId);
    }

    public GameDefinition getGameDefinitionByServerId(int serverId) {
        return mGameDefinitionLocalRepo.getGameDefinitionByServerId(serverId);
    }

    public void deleteGameDefinition(GameDefinition gameDefinition) {
        mGameDefinitionLocalRepo.deleteGameDefinitionByBoardGameGeekId(gameDefinition.getBoardGameGeekObjectId());
        if (gameDefinition.getThumbnailLocalPath() != null) {
            File localThumbnail = new File(gameDefinition.getThumbnailLocalPath());
            if (localThumbnail.exists()) {
                localThumbnail.delete();
            }
        }
    }

    public GameDefinition getGameDefinitionById(int id) {
        return mGameDefinitionLocalRepo.getGameDefinitionById(id);
    }

    public Observable<CreateGameDefinitionResponseDTO> saveRemoteGameDefinition(GameDefinition selectedGameDefinition) {
        return mGameDefinitionsRemoteRepo.createGameDefinition(selectedGameDefinition).retryWhen(mAccountManager.getRefreshUserSessionFunction());
    }

    public GameDefinition getGameDefinitionByBoardGameGeekIdAndGroupId(int boardGameObjectId, String gamingGroupId) {
        return mGameDefinitionLocalRepo.getGameDefinitionByBoardGameGeekIdAndGroupId(boardGameObjectId, gamingGroupId);
    }

    public Completable updateRemoteGameDefinition(GameDefinition gameDefinition) {
        return Completable.fromObservable(
                mGameDefinitionsRemoteRepo.updateGameDefinition(gameDefinition).retryWhen(mAccountManager.getRefreshUserSessionFunction())
        );
    }
}
