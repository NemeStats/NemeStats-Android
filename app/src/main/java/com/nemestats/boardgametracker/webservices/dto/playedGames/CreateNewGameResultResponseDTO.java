package com.nemestats.boardgametracker.webservices.dto.playedGames;

/**
 * Created by geomehedeniuc on 4/11/18.
 */

public class CreateNewGameResultResponseDTO {
    private int playedGameId;
    private int gamingGroupId;
    private String nemeStatsUrl;


    public int getPlayedGameId() {
        return playedGameId;
    }

    public int getGamingGroupId() {
        return gamingGroupId;
    }

    public String getNemeStatsUrl() {
        return nemeStatsUrl;
    }
}

