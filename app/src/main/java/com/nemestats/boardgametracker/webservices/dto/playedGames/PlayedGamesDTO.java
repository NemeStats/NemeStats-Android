package com.nemestats.boardgametracker.webservices.dto.playedGames;

import java.util.List;

/**
 * Created by mehegeo on 10/7/17.
 */

public class PlayedGamesDTO {


    private List<PlayedGameDTO> playedGames;

    public List<PlayedGameDTO> getPlayedGames() {
        return playedGames;
    }

    public class PlayedGameDTO {
        private List<PlayerGameResultDTO> playerGameResults;
        private List<ApplicationLinkageDTO> applicationLinkages;
        private int playedGameId;
        private int gameDefinitionId;
        private String gameDefinitionName;
        private String gamingGroupId;
        private String gamingGroupName;
        private String notes;
        private int boardGameGeekGameDefinitionId;
        private String datePlayed;
        private String dateLastUpdated;
        private String nemeStatsUrl;
        private String winnerType;

        public List<PlayerGameResultDTO> getPlayerGameResults() {
            return playerGameResults;
        }

        public List<ApplicationLinkageDTO> getApplicationLinkages() {
            return applicationLinkages;
        }

        public String getNemeStatsUrl() {
            return nemeStatsUrl;
        }

        public String getWinnerType() {
            return winnerType;
        }

        public int getPlayedGameId() {
            return playedGameId;
        }

        public int getGameDefinitionId() {
            return gameDefinitionId;
        }

        public String getGameDefinitionName() {
            return gameDefinitionName;
        }

        public String getGamingGroupId() {
            return gamingGroupId;
        }

        public String getGamingGroupName() {
            return gamingGroupName;
        }

        public String getNotes() {
            return notes;
        }

        public int getBoardGameGeekDefinitionId() {
            return boardGameGeekGameDefinitionId;
        }

        public String getDatePlayed() {
            return datePlayed;
        }

        public String getDateLastUpdated() {
            return dateLastUpdated;
        }

        public class PlayerGameResultDTO {
            private int playerId;
            private String playerName;
            private boolean playerActive;
            private int gameRank;
            private int pointsScored;
            private int nemeStatsPointsAwarded;
            private int gameWeightBonusNemePoints;
            private int gameDurationBonusNemePoints;
            private int totalNemeStatsPointsAwarded;

            public int getPlayerId() {
                return playerId;
            }

            public String getPlayerName() {
                return playerName;
            }

            public boolean isPlayerActive() {
                return playerActive;
            }

            public int getGameRank() {
                return gameRank;
            }

            public int getPointsScored() {
                return pointsScored;
            }

            public int getNemeStatsPointsAwarded() {
                return nemeStatsPointsAwarded;
            }

            public int getGameWeightBonusNemePoints() {
                return gameWeightBonusNemePoints;
            }

            public int getGameDurationBonusNemePoints() {
                return gameDurationBonusNemePoints;
            }

            public int getTotalNemeStatsPointsAwarded() {
                return totalNemeStatsPointsAwarded;
            }
        }
    }

}
