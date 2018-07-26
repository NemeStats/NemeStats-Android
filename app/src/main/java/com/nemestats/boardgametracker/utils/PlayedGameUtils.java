package com.nemestats.boardgametracker.utils;

import com.nemestats.boardgametracker.domain.PlayerGameResults;

import java.util.List;

/**
 * Created by geomehedeniuc on 4/15/18.
 */

public class PlayedGameUtils {
    public static final String RESULT_TYPE_RANKED = "Ranked";
    public static final String RESULT_TYPE_SCORED = "Scored";
    public static final String RESULT_TYPE_CO_OP = "CO-OP";

    public static final int TEAM_WIN_RANK = 1;
    public static final int TEAM_LOST_RANK = 2;

    public static String getGameTypeFromResults(List<PlayerGameResults> playerGameResultsList) {

        if (playerGameResultsList != null) {
            for (PlayerGameResults playerGameResult : playerGameResultsList) {
                if (playerGameResult.getPointsScored() != 0) {
                    return RESULT_TYPE_SCORED;
                }
            }

            int firstRank = 0;
            if (!playerGameResultsList.isEmpty()) {
                firstRank = playerGameResultsList.get(0).getGameRank();
            }
            for (PlayerGameResults playerGameResult : playerGameResultsList) {
                if (playerGameResult.getGameRank() != firstRank) {
                    return RESULT_TYPE_RANKED;
                }
            }
        }
        return RESULT_TYPE_CO_OP;
    }
}
