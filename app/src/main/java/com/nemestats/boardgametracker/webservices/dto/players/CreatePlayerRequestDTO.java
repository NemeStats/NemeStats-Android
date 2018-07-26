package com.nemestats.boardgametracker.webservices.dto.players;

/**
 * Created by geomehedeniuc on 5/13/18.
 */

public class CreatePlayerRequestDTO {

    String playerName;
    int gamingGroupId;
    String playerEmailAddress;

    public CreatePlayerRequestDTO(String playerName, int gamingGroupId, String playerEmailAddress) {
        this.playerName = playerName;
        this.gamingGroupId = gamingGroupId;
        this.playerEmailAddress = playerEmailAddress;
    }
}
