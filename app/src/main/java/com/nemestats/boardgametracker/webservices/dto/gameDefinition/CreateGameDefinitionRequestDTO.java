package com.nemestats.boardgametracker.webservices.dto.gameDefinition;

/**
 * Created by geomehedeniuc on 3/29/18.
 */

public class CreateGameDefinitionRequestDTO {

    private String gameDefinitionName;
    private int gamingGroupId;
    private String gameDefinitionDescription;
    private int boardGameGeekObjectId;

    public CreateGameDefinitionRequestDTO(String gameDefinitionName, int gamingGroupId, String gameDefinitionDescription, int boardGameGeekObjectId) {
        this.gameDefinitionName = gameDefinitionName;
        this.gamingGroupId = gamingGroupId;
        this.gameDefinitionDescription = gameDefinitionDescription;
        this.boardGameGeekObjectId = boardGameGeekObjectId;
    }
}
