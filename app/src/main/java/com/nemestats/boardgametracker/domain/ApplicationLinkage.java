package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;


/**
 * Created by mehegeo on 10/7/17.
 */

@DatabaseTable(tableName = ApplicationLinkage.TABLE_NAME)
public class ApplicationLinkage implements Serializable {
    public static final String TABLE_NAME = "applicationLinkage";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_APPLICATION_NAME = "applicationName";
    public static final String COLUMN_ENTITY_ID = "entityId";
    public static final String COLUMN_PARENT_PLAYED_GAME = "parentPlayedGame";


    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_APPLICATION_NAME)
    private String mApplicationName;

    @DatabaseField(columnName = COLUMN_ENTITY_ID)
    private String mEntityId;


    @DatabaseField(columnName = COLUMN_PARENT_PLAYED_GAME, foreign = true, foreignAutoRefresh = false)
    private PlayedGame mPlayedGame;


    public ApplicationLinkage() {
    }

    public ApplicationLinkage(String applicationName, String entityId) {
        mApplicationName = applicationName;
        mEntityId = entityId;
    }

    public PlayedGame getPlayedGame() {
        return mPlayedGame;
    }

    public void setPlayedGame(PlayedGame playedGame) {
        mPlayedGame = playedGame;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getApplicationName() {
        return mApplicationName;
    }

    public void setApplicationName(String applicationName) {
        mApplicationName = applicationName;
    }

    public String getEntityId() {
        return mEntityId;
    }

    public void setEntityId(String entityId) {
        mEntityId = entityId;
    }
}
