package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mehegeo on 9/26/17.
 */

@DatabaseTable(tableName = Player.TABLE_NAME)
public class Player implements Serializable {

    public static final String TABLE_NAME = "tableName";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SERVER_ID = "serverId";
    public static final String COLUMN_PLAYER_NAME = "playerName";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_CURRENT_NEMESIS_PLAYER_ID = "currentNemesisPlayerId";
    public static final String COLUMN_GAMING_GROUP_SERVER_ID = "gamingGroupServerId";
    public static final String COLUMN_NEMESTATS_URL = "nemestatsUrl";
    public static final String COLUMN_REMOTE_AVATAR_URL = "remoteAvatarUrl";
    public static final String COLUMN_LOCAL_AVATAR_URL = "localAvatarUrl";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_SERVER_ID)
    private int mServerId;

    @DatabaseField(columnName = COLUMN_PLAYER_NAME)
    private String mPlayerName;

    @DatabaseField(columnName = COLUMN_ACTIVE)
    private boolean mActive;

    @DatabaseField(columnName = COLUMN_CURRENT_NEMESIS_PLAYER_ID)
    private int mCurrentNemesisPlayerId;

    @DatabaseField(columnName = COLUMN_GAMING_GROUP_SERVER_ID)
    private String mGamingGroupServerId;

    @DatabaseField(columnName = COLUMN_NEMESTATS_URL)
    private String mNemestatsUrl;

    @DatabaseField(columnName = COLUMN_REMOTE_AVATAR_URL)
    private String mRemoteAvatarUrl;

    @DatabaseField(columnName = COLUMN_LOCAL_AVATAR_URL)
    private String mLocalAvatarUrl;

    private String mEmailAddress;

    public Player() {
    }

    public Player(int serverId, String playerName, boolean active, int currentNemesisPlayerId, String gamingGroupServerId, String nemestatsUrl) {
        mServerId = serverId;
        mPlayerName = playerName;
        mActive = active;
        mCurrentNemesisPlayerId = currentNemesisPlayerId;
        mGamingGroupServerId = gamingGroupServerId;
        mNemestatsUrl = nemestatsUrl;
    }

    public String getRemoteAvatarUrl() {
        return mRemoteAvatarUrl;
    }

    public void setRemoteAvatarUrl(String remoteAvatarUrl) {
        mRemoteAvatarUrl = remoteAvatarUrl;
    }

    public String getLocalAvatarUrl() {
        return mLocalAvatarUrl;
    }

    public void setLocalAvatarUrl(String localAvatarUrl) {
        mLocalAvatarUrl = localAvatarUrl;
    }

    public String getNemestatsUrl() {
        return mNemestatsUrl;
    }

    public void setNemestatsUrl(String nemestatsUrl) {
        mNemestatsUrl = nemestatsUrl;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        mEmailAddress = emailAddress;
    }

    public int getId() {
        return mId;
    }

    public String getGamingGroupServerId() {
        return mGamingGroupServerId;
    }

    public void setGamingGroupServerId(String gamingGroupServerId) {
        mGamingGroupServerId = gamingGroupServerId;
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

    public String getPlayerName() {
        return mPlayerName;
    }

    public void setPlayerName(String playerName) {
        mPlayerName = playerName;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }

    public int getCurrentNemesisPlayerId() {
        return mCurrentNemesisPlayerId;
    }

    public void setCurrentNemesisPlayerId(int currentNemesisPlayerId) {
        mCurrentNemesisPlayerId = currentNemesisPlayerId;
    }
}
