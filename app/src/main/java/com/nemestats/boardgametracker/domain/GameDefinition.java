package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mehegeo on 10/7/17.
 */


@DatabaseTable(tableName = GameDefinition.TABLE_NAME)
public class GameDefinition implements Serializable {
    public static final String TABLE_NAME = "gameDefinitions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_GAME_NAME = "gameDefinitionName";
    public static final String COLUMN_SERVER_ID = "serverId";
    public static final String COLUMN_BOARD_GAME_GEEK_OBJECT_ID = "boardGameGeekObjectId";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_GAMING_GROUP_ID = "gamingGroupId";
    public static final String COLUMN_REMOTE_THUMBNAIL_PATH = "thumbnailRemotePath";
    public static final String COLUMN_LOCAL_THUMBNAIL_PATH = "thumbnailLocalPath";
    public static final String COLUMN_NEMESTATS_URL = "nemestatsUrl";

    public static final String COLUMN_MIN_PLAYERS = "minPlayers";
    public static final String COLUMN_MAX_PLAYERS = "maxPlayers";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_MIN_PLAY_TIME = "minPlayTime";
    public static final String COLUMN_MAX_PLAY_TIME = "maxPlatTime";
    public static final String COLUMN_YEAR_PUBLISHED = "yearPublished";

    public static final String COLUMN_CATEGORIES = "categories";
    public static final String COLUMN_MECHANICS = "mechanics";
    public static final String CATEGORY_DIVIDER = " | ";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_GAME_NAME)
    private String mGameDefinitionName;

    @DatabaseField(columnName = COLUMN_SERVER_ID)
    private int mServerId;

    @DatabaseField(columnName = COLUMN_GAMING_GROUP_ID)
    private String mGamingGroupId;

    @DatabaseField(columnName = COLUMN_BOARD_GAME_GEEK_OBJECT_ID)
    private int mBoardGameGeekObjectId;

    @DatabaseField(columnName = COLUMN_ACTIVE)
    private boolean mActive;

    @DatabaseField(columnName = COLUMN_REMOTE_THUMBNAIL_PATH)
    private String mThumbnailRemotePath;

    @DatabaseField(columnName = COLUMN_LOCAL_THUMBNAIL_PATH)
    private String mThumbnailLocalPath;

    @DatabaseField(columnName = COLUMN_MIN_PLAYERS)
    private int mMinPlayers;

    @DatabaseField(columnName = COLUMN_MAX_PLAYERS)
    private int mMaxPlayers;

    @DatabaseField(columnName = COLUMN_DESCRIPTION)
    private String mDescription;

    @DatabaseField(columnName = COLUMN_MIN_PLAY_TIME)
    private float mMinPlayTime;

    @DatabaseField(columnName = COLUMN_MAX_PLAY_TIME)
    private float mMaxPlayTime;

    @DatabaseField(columnName = COLUMN_YEAR_PUBLISHED)
    private String mYearPublished;

    @DatabaseField(columnName = COLUMN_CATEGORIES)
    private String mCategories;

    @DatabaseField(columnName = COLUMN_MECHANICS)
    private String mMechanics;

    @DatabaseField(columnName = COLUMN_NEMESTATS_URL)
    private String mNemestatsUrl;


    public GameDefinition() {

    }

    public GameDefinition(String gameDefinitionName, int serverId, int boardGameGeekObjectId, boolean active, String nemestatsUrl) {
        mGameDefinitionName = gameDefinitionName;
        mServerId = serverId;
        mBoardGameGeekObjectId = boardGameGeekObjectId;
        mActive = active;
        mNemestatsUrl = nemestatsUrl;
    }


    public String getNemestatsUrl() {
        return mNemestatsUrl;
    }

    public void setNemestatsUrl(String nemestatsUrl) {
        mNemestatsUrl = nemestatsUrl;
    }

    public String getCategories() {
        return mCategories;
    }

    public void setCategories(String categories) {
        mCategories = categories;
    }

    public String getMechanics() {
        return mMechanics;
    }

    public void setMechanics(String mechanics) {
        mMechanics = mechanics;
    }

    public int getMinPlayers() {
        return mMinPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        mMinPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return mMaxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        mMaxPlayers = maxPlayers;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public float getMinPlayTime() {
        return mMinPlayTime;
    }

    public void setMinPlayTime(float minPlayTime) {
        mMinPlayTime = minPlayTime;
    }

    public float getMaxPlayTime() {
        return mMaxPlayTime;
    }

    public void setMaxPlayTime(float maxPlayTime) {
        mMaxPlayTime = maxPlayTime;
    }

    public String getYearPublished() {
        return mYearPublished;
    }

    public void setYearPublished(String yearPublished) {
        mYearPublished = yearPublished;
    }

    public String getThumbnailRemotePath() {

        return mThumbnailRemotePath;
    }

    public void setThumbnailRemotePath(String thumbnailRemotePath) {
        mThumbnailRemotePath = thumbnailRemotePath;
    }

    public String getThumbnailLocalPath() {
        return mThumbnailLocalPath;
    }

    public void setThumbnailLocalPath(String thumbnailLocalPath) {
        mThumbnailLocalPath = thumbnailLocalPath;
    }

    public int getId() {
        return mId;
    }

    public String getGamingGroupId() {
        return mGamingGroupId;
    }

    public void setGamingGroupId(String gamingGroupId) {
        mGamingGroupId = gamingGroupId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getGameDefinitionName() {
        return mGameDefinitionName;
    }

    public void setGameDefinitionName(String gameDefinitionName) {
        mGameDefinitionName = gameDefinitionName;
    }

    public int getServerId() {
        return mServerId;
    }

    public void setServerId(int serverId) {
        mServerId = serverId;
    }

    public int getBoardGameGeekObjectId() {
        return mBoardGameGeekObjectId;
    }

    public void setBoardGameGeekObjectId(int boardGameGeekObjectId) {
        mBoardGameGeekObjectId = boardGameGeekObjectId;
    }

    public boolean isActive() {
        return mActive;
    }

    public void setActive(boolean active) {
        mActive = active;
    }
}
