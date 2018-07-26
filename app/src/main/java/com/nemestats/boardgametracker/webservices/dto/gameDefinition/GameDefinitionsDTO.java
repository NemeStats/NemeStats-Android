package com.nemestats.boardgametracker.webservices.dto.gameDefinition;

import java.util.List;

/**
 * Created by mehegeo on 10/7/17.
 */

public class GameDefinitionsDTO {

    private List<GameDefinitioDTO> gameDefinitions;

    public List<GameDefinitioDTO> getGameDefinitions() {
        return gameDefinitions;
    }

    public class GameDefinitioDTO {
        private String gameDefinitionName;
        private int gameDefinitionId;
        private int boardGameGeekGameDefinitionId;
        private boolean active;
        private String nemeStatsUrl;

        public String getGameDefinitionName() {
            return gameDefinitionName;
        }

        public int getGameDefinitionId() {
            return gameDefinitionId;
        }

        public int getBoardGameGeekObjectId() {
            return boardGameGeekGameDefinitionId;
        }

        public String getNemeStatsUrl() {
            return nemeStatsUrl;
        }

        public boolean isActive() {
            return active;
        }
    }
}
