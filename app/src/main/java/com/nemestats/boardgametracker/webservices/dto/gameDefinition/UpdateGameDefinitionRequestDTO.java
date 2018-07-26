package com.nemestats.boardgametracker.webservices.dto.gameDefinition;

/**
 * Created by geomehedeniuc on 5/20/18.
 */

public class UpdateGameDefinitionRequestDTO {
    private String gameDefinitionName;
    private boolean active;

    public UpdateGameDefinitionRequestDTO(String gameDefinitionName, boolean active) {
        this.gameDefinitionName = gameDefinitionName;
        this.active = active;
    }
}
