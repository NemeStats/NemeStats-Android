package com.nemestats.boardgametracker.webservices.dto.playedGames;

import java.util.List;

/**
 * Created by geomehedeniuc on 4/11/18.
 */

public class CreateNewGameResultRequestDTO {
    private int gameDefinitionId;
    private int gamingGroupId;
    private String datePlayed;
    private String notes;
    List<PlayerRanks> playerRanks;
    List<ApplicationLinkageDTO> applicationLinkages;

    public void setGameDefinitionId(int gameDefinitionId) {
        this.gameDefinitionId = gameDefinitionId;
    }

    public void setGamingGroupId(int gamingGroupId) {
        this.gamingGroupId = gamingGroupId;
    }

    public void setDatePlayed(String datePlayed) {
        this.datePlayed = datePlayed;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPlayerRanks(List<PlayerRanks> playerRanks) {
        this.playerRanks = playerRanks;
    }

    public void setApplicationLinkages(List<ApplicationLinkageDTO> applicationLinkages) {
        this.applicationLinkages = applicationLinkages;
    }

    public List<PlayerRanks> getPlayerRanks() {
        return playerRanks;
    }

    public static class PlayerRanks {
        private int playerId;
        private int gameRank;
        private float pointsScored;

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public void setGameRank(int gameRank) {
            this.gameRank = gameRank;
        }

        public void setPointsScored(float pointsScored) {
            this.pointsScored = pointsScored;
        }
    }
}
