package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mehegeo on 9/26/17.
 */

@DatabaseTable(tableName = PlayedGame.TABLE_NAME)
public class PlayedGame implements Serializable {

    public static final String TABLE_NAME = "playedGame";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SERVER_ID = "serverId";
    public static final String COLUMN_GAME_DEFINITION_ID = "gameDefinitionId";
    public static final String COLUMN_GAME_DEFINITION_NAME = "gameDefinitionName";
    public static final String COLUMN_BOARD_GAME_GEEK_OBJECT_ID = "boardGameGeekObjectId";
    public static final String COLUMN_GAMING_GROUP_ID = "gamingGroupId";
    public static final String COLUMN_GAMING_GROUP_NAME = "gamingGroupName";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_DATE_PLAYED = "datePlayed";
    public static final String COLUMN_LAST_TIME_UPDATED = "lastTimeUpdated";
    public static final String COLUMN_PLAYER_GAME_RESULTS_LIST = "playerGameResultsList";
    public static final String COLUMN_APPLICATION_LINKAGES = "applicationLinkages";
    public static final String COLUMN_GAME_RESULT_TYPE = "gameResultType";
    public static final String COLUMN_NEMESTATS_URL = "nemestatsUrl";
    public static final String COLUMN_WINNER_TYPE = "winnerType";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_SERVER_ID)
    private int mServerId;

    @DatabaseField(columnName = COLUMN_GAME_DEFINITION_ID)
    private int mGameDefinitionId;

    @DatabaseField(columnName = COLUMN_GAME_DEFINITION_NAME)
    private String mGameDefinitionName;

    @DatabaseField(columnName = COLUMN_BOARD_GAME_GEEK_OBJECT_ID)
    private int mBoardGameGeekObjectId;

    @DatabaseField(columnName = COLUMN_GAMING_GROUP_ID)
    private String mGamingGroupId;

    @DatabaseField(columnName = COLUMN_GAMING_GROUP_NAME)
    private String mGamingGroupName;

    @DatabaseField(columnName = COLUMN_NOTES)
    private String mNotes;

    @DatabaseField(columnName = COLUMN_DATE_PLAYED)
    private DateTime mDatePlayed;

    @DatabaseField(columnName = COLUMN_LAST_TIME_UPDATED)
    private DateTime mLastTimeUpdated;

    @DatabaseField(columnName = COLUMN_GAME_RESULT_TYPE)
    private String mGameResultType;

    @DatabaseField(columnName = COLUMN_NEMESTATS_URL)
    private String mNemestatsUrl;

    @DatabaseField(columnName = COLUMN_WINNER_TYPE)
    private String mWinnerType;

    @ForeignCollectionField(columnName = COLUMN_PLAYER_GAME_RESULTS_LIST, eager = false)
    private ForeignCollection<PlayerGameResults> mPlayerGameResultsList;


    @ForeignCollectionField(columnName = COLUMN_APPLICATION_LINKAGES, eager = false)
    private ForeignCollection<ApplicationLinkage> mApplicationLinkagesList;

    private List<PlayerGameResults> mPlayerGameResultsListInMemory;
    private List<ApplicationLinkage> mApplicationLinkagesListInMemory;

    public PlayedGame() {

    }

    public PlayedGame(int playedGameId,
                      int gameDefinitionId,
                      String gameDefinitionName,
                      int boardGameGeekObjectId,
                      String gamingGroupId,
                      String gamingGroupName,
                      String notes,
                      DateTime datePlayed,
                      DateTime lastTimeUpdated,
                      String winnerType,
                      String nemestatsUrl) {
        mServerId = playedGameId;
        mGameDefinitionId = gameDefinitionId;
        mGameDefinitionName = gameDefinitionName;
        mBoardGameGeekObjectId = boardGameGeekObjectId;
        mGamingGroupId = gamingGroupId;
        mGamingGroupName = gamingGroupName;
        mNotes = notes;
        mDatePlayed = datePlayed;
        mLastTimeUpdated = lastTimeUpdated;
        mWinnerType = winnerType;
        mNemestatsUrl = nemestatsUrl;
    }

    public List<ApplicationLinkage> getApplicationLinkagesList() {
        if (mApplicationLinkagesListInMemory == null) {
            mApplicationLinkagesListInMemory = new ArrayList<>();
            if (mApplicationLinkagesList != null) {
                for (ApplicationLinkage applicationLinkage : mApplicationLinkagesList) {
                    mApplicationLinkagesListInMemory.add(applicationLinkage);
                }
            }
        }
        return mApplicationLinkagesListInMemory;
    }

    public String getWinnerType() {
        return mWinnerType;
    }

    public void setWinnerType(String winnerType) {
        mWinnerType = winnerType;
    }

    public List<PlayerGameResults> getPlayerGameResultsList() {
        if (mPlayerGameResultsListInMemory == null) {
            mPlayerGameResultsListInMemory = new ArrayList<>();
            if (mPlayerGameResultsList != null) {
                for (PlayerGameResults statusAudit : mPlayerGameResultsList) {
                    mPlayerGameResultsListInMemory.add(statusAudit);
                }
            }
        }
        return mPlayerGameResultsListInMemory;
    }

    public String getNemestatsUrl() {
        return mNemestatsUrl;
    }

    public void setNemestatsUrl(String nemestatsUrl) {
        mNemestatsUrl = nemestatsUrl;
    }

    public String getGameResultType() {
        return mGameResultType;
    }

    public void setGameResultType(String gameResultType) {
        mGameResultType = gameResultType;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getServerId() {
        return mServerId;
    }

    public void setServerId(int serverId) {
        mServerId = serverId;
    }

    public int getGameDefinitionId() {
        return mGameDefinitionId;
    }

    public void setGameDefinitionId(int gameDefinitionId) {
        mGameDefinitionId = gameDefinitionId;
    }

    public String getGameDefinitionName() {
        return mGameDefinitionName;
    }

    public void setGameDefinitionName(String gameDefinitionName) {
        mGameDefinitionName = gameDefinitionName;
    }

    public int getBoardGameGeekObjectId() {
        return mBoardGameGeekObjectId;
    }

    public void setBoardGameGeekObjectId(int boardGameGeekObjectId) {
        mBoardGameGeekObjectId = boardGameGeekObjectId;
    }

    public String getGamingGroupId() {
        return mGamingGroupId;
    }

    public void setGamingGroupId(String gamingGroupId) {
        mGamingGroupId = gamingGroupId;
    }

    public String getGamingGroupName() {
        return mGamingGroupName;
    }

    public void setGamingGroupName(String gamingGroupName) {
        mGamingGroupName = gamingGroupName;
    }

    public String getNotes() {
        return mNotes;
    }

    public void setNotes(String notes) {
        mNotes = notes;
    }

    public DateTime getDatePlayed() {
        return mDatePlayed;
    }

    public void setDatePlayed(DateTime datePlayed) {
        mDatePlayed = datePlayed;
    }

    public DateTime getLastTimeUpdated() {
        return mLastTimeUpdated;
    }

    public void setLastTimeUpdated(DateTime lastTimeUpdated) {
        mLastTimeUpdated = lastTimeUpdated;
    }
}
