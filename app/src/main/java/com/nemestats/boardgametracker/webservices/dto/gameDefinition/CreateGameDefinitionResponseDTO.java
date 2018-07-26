package com.nemestats.boardgametracker.webservices.dto.gameDefinition;

/**
 * Created by geomehedeniuc on 3/29/18.
 */

public class CreateGameDefinitionResponseDTO {

    private int gameDefinitionId;
    private int gamingGroupId;
    private String nemeStatsUrl;

    public int getGameDefinitionId() {
        return gameDefinitionId;
    }

    public int getGamingGroupId() {
        return gamingGroupId;
    }

    public String getNemeStatsUrl() {
        return nemeStatsUrl;
    }
}
