package com.nemestats.boardgametracker.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by mehegeo on 10/5/17.
 */

@DatabaseTable(tableName = GamingGroup.TABLE_NAME)
public class GamingGroup implements Serializable {

    public static final String TABLE_NAME = "gamingGroups";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SERVER_ID = "serverId";
    public static final String COLUMN_GROUP_NAME = "groupName";
    public static final String COLUMN_PUBLIC_DESCRIPTION = "publicDescription";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_NEMESTATS_URL = "nemestatsUrl";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = COLUMN_SERVER_ID)
    private String mServerId;

    @DatabaseField(columnName = COLUMN_GROUP_NAME)
    private String mGroupName;

    @DatabaseField(columnName = COLUMN_PUBLIC_DESCRIPTION)
    private String mPublicDescription;

    @DatabaseField(columnName = COLUMN_ACTIVE)
    private boolean mIsActive;

    @DatabaseField(columnName = COLUMN_NEMESTATS_URL)
    private String mNemestatsUrl;

    public GamingGroup() {

    }

    public GamingGroup(String serverId, String groupName, String publicDescription, boolean isActive, String nemestatsUrl) {
        mServerId = serverId;
        mGroupName = groupName;
        mPublicDescription = publicDescription;
        mIsActive = isActive;
        mNemestatsUrl = nemestatsUrl;
    }

    public String getNemestatsUrl() {
        return mNemestatsUrl;
    }

    public void setNemestatsUrl(String nemestatsUrl) {
        mNemestatsUrl = nemestatsUrl;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean active) {
        mIsActive = active;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getServerId() {
        return mServerId;
    }

    public void setServerId(String serverId) {
        mServerId = serverId;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public String getPublicDescription() {
        return mPublicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        mPublicDescription = publicDescription;
    }

    @Override
    public String toString() {
        return mGroupName;
    }
}
