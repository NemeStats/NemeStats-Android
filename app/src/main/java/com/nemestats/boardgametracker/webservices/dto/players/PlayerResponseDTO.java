package com.nemestats.boardgametracker.webservices.dto.players;

/**
 * Created by geomehedeniuc on 5/13/18.
 */

public class PlayerResponseDTO {
    private String playerName;
    private int playerId;
    private boolean active;
    private int currentNemesisPlayerId;
    private String nemeStatsUrl;
    private String registeredUserGravatarUrl;

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isActive() {
        return active;
    }

    public int getCurrentNemesisPlayerId() {
        return currentNemesisPlayerId;
    }

    public String getNemeStatsUrl() {
        return nemeStatsUrl;
    }

    public String getRegisteredUserGravatarUrl() {
        return registeredUserGravatarUrl;
    }
}
