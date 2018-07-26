package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mehegeo on 10/15/17.
 */

@DatabaseTable(tableName = UserPlayer.COLUMN_TABLE_NAME)
public class UserPlayer {
    public static final String COLUMN_TABLE_NAME = "userPlayer";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYER_SERVER_ID = "playerServerId";
    public static final String COLUMN_PLAYER_NAME = "playerName";
    public static final String COLUMN_GAMING_GROUP_ID = "gamingGroupId";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_PLAYER_SERVER_ID)
    private int mPlayerServerId;

    @DatabaseField(columnName = COLUMN_PLAYER_NAME)
    private String mPlayerName;

    @DatabaseField(columnName = COLUMN_GAMING_GROUP_ID)
    private int mGamingGroupId;


    public UserPlayer() {
    }

    public UserPlayer(int playerServerId, String playerName, int gamingGroupId) {
        mPlayerServerId = playerServerId;
        mPlayerName = playerName;
        mGamingGroupId = gamingGroupId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getPlayerServerId() {
        return mPlayerServerId;
    }

    public void setPlayerServerId(int playerServerId) {
        mPlayerServerId = playerServerId;
    }

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public int getGamingGroupId() {
        return mGamingGroupId;
    }

    public void setGamingGroupId(int gamingGroupId) {
        mGamingGroupId = gamingGroupId;
    }
}
