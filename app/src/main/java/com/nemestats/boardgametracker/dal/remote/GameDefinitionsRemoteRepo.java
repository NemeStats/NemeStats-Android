package com.nemestats.boardgametracker.dal.remote;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.webservices.NemeStatsServicesClientWithAuthentication;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.GameDefinitionsDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.UpdateGameDefinitionRequestDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/7/17.
 */

public class GameDefinitionsRemoteRepo {

    private NemeStatsServicesClientWithAuthentication mNemeStatsServicesClientWithAuthentication;

    @Inject
    public GameDefinitionsRemoteRepo(NemeStatsServicesClientWithAuthentication nemeStatsServicesClientWithAuthentication) {
        mNemeStatsServicesClientWithAuthentication = nemeStatsServicesClientWithAuthentication;
    }

    public Observable<List<GameDefinition>> getGameDefinitionsForGamingGroup(String gamingGroupId) {
        return mNemeStatsServicesClientWithAuthentication.getService().getGameDefinitionsForGamingGroup(gamingGroupId).map(gameDefinitionsDTO -> {
            List<GameDefinition> gameDefinitionList = new ArrayList<>();
            for (GameDefinitionsDTO.GameDefinitioDTO gameDefinitionDTO : gameDefinitionsDTO.getGameDefinitions()) {

                GameDefinition gameDefinition = new GameDefinition(
                        gameDefinitionDTO.getGameDefinitionName(),
                        gameDefinitionDTO.getGameDefinitionId(),
                        gameDefinitionDTO.getBoardGameGeekObjectId(),
                        gameDefinitionDTO.isActive(),
                        gameDefinitionDTO.getNemeStatsUrl()
                );
                gameDefinition.setGamingGroupId(gamingGroupId);
                gameDefinitionList.add(gameDefinition);
            }
            return gameDefinitionList;
        });
    }

    public Observable<CreateGameDefinitionResponseDTO> createGameDefinition(GameDefinition game) {
        CreateGameDefinitionRequestDTO createGameDefinitionRequestDTO = new CreateGameDefinitionRequestDTO(
                game.getGameDefinitionName(),
                Integer.parseInt(game.getGamingGroupId()),
                null,
                game.getBoardGameGeekObjectId()
        );

        return mNemeStatsServicesClientWithAuthentication.getService().createGameDefinition(createGameDefinitionRequestDTO);
    }

    public Observable<Void> updateGameDefinition(GameDefinition gameDefinition) {
        UpdateGameDefinitionRequestDTO updateGameDefinitionRequestDTO = new UpdateGameDefinitionRequestDTO(gameDefinition.getGameDefinitionName(), gameDefinition.isActive());
        return mNemeStatsServicesClientWithAuthentication.getService().updateGameDefinition(updateGameDefinitionRequestDTO, String.valueOf(gameDefinition.getServerId()));
    }
}
