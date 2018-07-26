package com.nemestats.boardgametracker.webservices.dto.players;

/**
 * Created by mehegeo on 10/5/17.
 */

public class UserPlayerDTO {
    private int playerId;
    private String playerName;
    private int gamingGroupId;

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGamingGroupId() {
        return gamingGroupId;
    }

}
