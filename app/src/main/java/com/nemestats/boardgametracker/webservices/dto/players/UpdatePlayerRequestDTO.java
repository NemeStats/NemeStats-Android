package com.nemestats.boardgametracker.webservices.dto.players;

/**
 * Created by geomehedeniuc on 5/20/18.
 */

public class UpdatePlayerRequestDTO {
    private String playerName;
    private boolean active;

    public UpdatePlayerRequestDTO(String playerName, boolean active) {
        this.playerName = playerName;
        this.active = active;
    }
}
