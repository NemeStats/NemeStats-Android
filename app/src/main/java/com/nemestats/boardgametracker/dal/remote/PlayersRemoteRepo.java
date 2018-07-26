package com.nemestats.boardgametracker.dal.remote;

import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.webservices.NemeStatsServicesClientWithAuthentication;
import com.nemestats.boardgametracker.webservices.dto.players.CreatePlayerRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.players.PlayerResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.players.UpdatePlayerRequestDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/6/17.
 */

public class PlayersRemoteRepo {

    private NemeStatsServicesClientWithAuthentication mNemeStatsServicesClientWithAuthentication;

    @Inject
    public PlayersRemoteRepo(NemeStatsServicesClientWithAuthentication nemeStatsServicesClientWithAuthentication) {
        mNemeStatsServicesClientWithAuthentication = nemeStatsServicesClientWithAuthentication;
    }

    public Observable<List<Player>> getPlayersInGamingGroup(String gamingGroupId) {
        return mNemeStatsServicesClientWithAuthentication.getService().getPlayersInGamingGroup(gamingGroupId)
                .map(playersDTO -> {
                    List<Player> playerList = new ArrayList<>();
                    for (PlayerResponseDTO playerDTO : playersDTO.getPlayers()) {
                        Player player = new Player(playerDTO.getPlayerId(), playerDTO.getPlayerName(), playerDTO.isActive(), playerDTO.getCurrentNemesisPlayerId(), gamingGroupId, playerDTO.getNemeStatsUrl());
                        player.setGamingGroupServerId(gamingGroupId);
                        player.setRemoteAvatarUrl(playerDTO.getRegisteredUserGravatarUrl());
                        playerList.add(player);
                    }
                    return playerList;
                });
    }

    public Observable<PlayerResponseDTO> savePlayer(CreatePlayerRequestDTO createPlayerRequestDTO) {
        return mNemeStatsServicesClientWithAuthentication.getService().savePlayer(createPlayerRequestDTO);
    }

    public Observable<Void> updatePlayer(UpdatePlayerRequestDTO updatePlayerRequestDTO, int serverId) {
        return mNemeStatsServicesClientWithAuthentication.getService().updatePlayer(updatePlayerRequestDTO, String.valueOf(serverId));
    }
}
