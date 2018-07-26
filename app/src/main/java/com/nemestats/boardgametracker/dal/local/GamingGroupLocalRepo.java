package com.nemestats.boardgametracker.dal.local;

import com.nemestats.boardgametracker.domain.GamingGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/5/17.
 */

public class GamingGroupLocalRepo {

    private DatabaseHelper mDatabaseHelper;

    @Inject
    public GamingGroupLocalRepo(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }

    public void createOrUpdate(GamingGroup gamingGroup) {
        if (gamingGroup != null) {
            try {
                GamingGroup localGamingGroup = getGamingGroupByServerId(gamingGroup.getServerId());
                if (localGamingGroup != null) {
                    gamingGroup.setId(localGamingGroup.getId());
                    mDatabaseHelper.getDao(GamingGroup.class).update(gamingGroup);
                } else {
                    mDatabaseHelper.getDao(GamingGroup.class).create(gamingGroup);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void delete(GamingGroup gamingGroup) {
        try {
            mDatabaseHelper.getDao(GamingGroup.class).delete(gamingGroup);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<GamingGroup> getAll(boolean activeOnly) {
        List<GamingGroup> gamingGroupList = new ArrayList<>();
        try {
            if (activeOnly) {
                gamingGroupList.addAll(mDatabaseHelper.getDao(GamingGroup.class).queryBuilder().where().eq(GamingGroup.COLUMN_ACTIVE, true).query());
            } else {
                gamingGroupList.addAll(mDatabaseHelper.getDao(GamingGroup.class).queryForAll());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gamingGroupList;
    }

    public GamingGroup getGamingGroupByServerId(String serverId) {
        if (serverId != null) {
            try {
                return mDatabaseHelper.getDao(GamingGroup.class).queryBuilder().where().eq(GamingGroup.COLUMN_SERVER_ID, serverId).queryForFirst();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
