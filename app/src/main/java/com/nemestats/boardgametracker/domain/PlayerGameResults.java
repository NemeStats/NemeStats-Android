package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;


/**
 * Created by mehegeo on 9/26/17.
 */

@DatabaseTable(tableName = PlayerGameResults.TABLE_NAME)
public class PlayerGameResults implements Serializable {

    public static final String TABLE_NAME = "playerGamerResults";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYER_SERVER_ID = "playerServerId";
    public static final String COLUMN_PLAYER_NAME = "playerName";
    public static final String COLUMN_PLAYER_ACTIVE = "player active";
    public static final String COLUMN_GAME_RANK = "gameRank";
    public static final String COLUMN_POINTS_SCORED = "pointsScored";
    public static final String COLUMN_NEMESTATS_POINTS_AWARDED = "nemestatsPointsAwarded";
    public static final String COLUMN_GAME_WEIGHT_BONUS_NEME_POINTS = "gameWeightBonusNemePoints";
    public static final String COLUMN_GAME_DURATION_BONUS_NEME_POINTS = "gameDurationBonusNemePoints";
    public static final String COLUMN_TOTAL_NEME_STATS_POINTS_AWARDED = "totalNemeStatsPointsAwarded";
    public static final String COLUMN_PARENT_PLAYED_GAME = "parentPlayedGame";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_PLAYER_SERVER_ID)
    private int mPlayerId;

    @DatabaseField(columnName = COLUMN_PLAYER_NAME)
    private String mPlayerName;

    @DatabaseField(columnName = COLUMN_PLAYER_ACTIVE)
    private boolean mPlayerActive;

    @DatabaseField(columnName = COLUMN_GAME_RANK)
    private int mGameRank;

    @DatabaseField(columnName = COLUMN_POINTS_SCORED)
    private float mPointsScored;

    @DatabaseField(columnName = COLUMN_NEMESTATS_POINTS_AWARDED)
    private int mNemeStatsPointsAwarded;

    @DatabaseField(columnName = COLUMN_GAME_WEIGHT_BONUS_NEME_POINTS)
    private int mGameWeightBonusNemePoints;

    @DatabaseField(columnName = COLUMN_GAME_DURATION_BONUS_NEME_POINTS)
    private int mGameDurationBonusNemePoints;

    @DatabaseField(columnName = COLUMN_TOTAL_NEME_STATS_POINTS_AWARDED)
    private int mTotalNemeStatsPointsAwarded;

    @DatabaseField(columnName = COLUMN_PARENT_PLAYED_GAME, foreign = true, foreignAutoRefresh = false)
    private PlayedGame mPlayedGame;

    public PlayerGameResults() {

    }

    public PlayerGameResults(int playerId, String playerName, boolean playerActive, int gameRank,
                             float pointsScored, int nemeStatsPointsAwarded, int gameWeightBonusNemePoints,
                             int gameDurationBonusNemePoints, int totalNemeStatsPointsAwarded) {
        mPlayerId = playerId;
        mPlayerName = playerName;
        mPlayerActive = playerActive;
        mGameRank = gameRank;
        mPointsScored = pointsScored;
        mNemeStatsPointsAwarded = nemeStatsPointsAwarded;
        mGameWeightBonusNemePoints = gameWeightBonusNemePoints;
        mGameDurationBonusNemePoints = gameDurationBonusNemePoints;
        mTotalNemeStatsPointsAwarded = totalNemeStatsPointsAwarded;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setPlayedGame(PlayedGame playedGame) {
        mPlayedGame = playedGame;
    }

    public PlayedGame getPlayedGame() {
        return mPlayedGame;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    public void setPlayerId(int playerId) {
        mPlayerId = playerId;
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public boolean isPlayerActive() {
        return mPlayerActive;
    }

    public void setPlayerActive(boolean playerActive) {
        mPlayerActive = playerActive;
    }

    public int getGameRank() {
        return mGameRank;
    }

    public void setGameRank(int gameRank) {
        mGameRank = gameRank;
    }

    public float getPointsScored() {
        return mPointsScored;
    }

    public void setPointsScored(float pointsScored) {
        mPointsScored = pointsScored;
    }

    public int getNemeStatsPointsAwarded() {
        return mNemeStatsPointsAwarded;
    }

    public void setNemeStatsPointsAwarded(int nemeStatsPointsAwarded) {
        mNemeStatsPointsAwarded = nemeStatsPointsAwarded;
    }

    public int getGameWeightBonusNemePoints() {
        return mGameWeightBonusNemePoints;
    }

    public void setGameWeightBonusNemePoints(int gameWeightBonusNemePoints) {
        mGameWeightBonusNemePoints = gameWeightBonusNemePoints;
    }

    public int getGameDurationBonusNemePoints() {
        return mGameDurationBonusNemePoints;
    }

    public void setGameDurationBonusNemePoints(int gameDurationBonusNemePoints) {
        mGameDurationBonusNemePoints = gameDurationBonusNemePoints;
    }

    public int getTotalNemeStatsPointsAwarded() {
        return mTotalNemeStatsPointsAwarded;
    }

    public void setTotalNemeStatsPointsAwarded(int totalNemeStatsPointsAwarded) {
        mTotalNemeStatsPointsAwarded = totalNemeStatsPointsAwarded;
    }
}
