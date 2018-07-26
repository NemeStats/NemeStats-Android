package com.nemestats.boardgametracker.managers;

import com.nemestats.boardgametracker.dal.local.PlayersLocalRepo;
import com.nemestats.boardgametracker.dal.remote.PlayersRemoteRepo;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.webservices.dto.players.CreatePlayerRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.players.PlayerResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.players.UpdatePlayerRequestDTO;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/6/17.
 */

public class PlayersManager {

    private PlayersRemoteRepo mPlayersRemoteRepo;
    private PlayersLocalRepo mPlayersLocalRepo;
    private AccountManager mAccountManager;

    @Inject
    public PlayersManager(PlayersRemoteRepo playersRemoteRepo, PlayersLocalRepo playersLocalRepo, AccountManager accountManager) {
        mPlayersRemoteRepo = playersRemoteRepo;
        mPlayersLocalRepo = playersLocalRepo;
        mAccountManager = accountManager;
    }

    public Observable<List<Player>> getPlayersInGamingGroup(String gamingGroupId) {
        return mPlayersRemoteRepo.getPlayersInGamingGroup(gamingGroupId).retryWhen(mAccountManager.getRefreshUserSessionFunction());
    }

    public List<Player> getLocalPlayersInGamingGroup(String gamingGroupServerId, boolean activeOnly) {
        return mPlayersLocalRepo.getPlayersInGamingGroupId(gamingGroupServerId, activeOnly);
    }

    public long getLocalNumberOfPlayersInGamingGroup(String gamingGroupId) {
        return mPlayersLocalRepo.getNumberOfPlayersInGamingGroup(gamingGroupId);
    }

    public Player getPlayerById(int id) {
        return mPlayersLocalRepo.getPlayerById(id);
    }

    public void savePlayers(List<Player> playersList) {
        mPlayersLocalRepo.savePlayers(playersList);
    }

    public Observable<PlayerResponseDTO> saveRemotePlayer(Player player) {
        CreatePlayerRequestDTO createPlayerRequestDTO = new CreatePlayerRequestDTO(player.getPlayerName(), Integer.valueOf(player.getGamingGroupServerId()), player.getEmailAddress());
        return mPlayersRemoteRepo.savePlayer(createPlayerRequestDTO)
                .retryWhen(mAccountManager.getRefreshUserSessionFunction());
    }

    public Completable updateRemotePlayer(Player player) {
        UpdatePlayerRequestDTO updatePlayerRequestDTO = new UpdatePlayerRequestDTO(player.getPlayerName(), player.isActive());
        return Completable.fromObservable(
                mPlayersRemoteRepo.updatePlayer(updatePlayerRequestDTO, player.getServerId())
                        .retryWhen(mAccountManager.getRefreshUserSessionFunction())
        );
    }
}
