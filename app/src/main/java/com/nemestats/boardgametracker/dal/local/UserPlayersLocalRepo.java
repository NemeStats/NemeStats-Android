package com.nemestats.boardgametracker.dal.local;

import com.nemestats.boardgametracker.domain.UserPlayer;

import java.sql.SQLException;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/15/17.
 */

public class UserPlayersLocalRepo {

    private DatabaseHelper mDatabaseHelper;

    @Inject
    public UserPlayersLocalRepo(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }


    public void createOrUpdate(UserPlayer userPlayer) {
        try {
            UserPlayer localUserPlayer = mDatabaseHelper.getDao(UserPlayer.class).queryBuilder().where().eq(UserPlayer.COLUMN_PLAYER_SERVER_ID, userPlayer.getPlayerServerId()).queryForFirst();
            if (localUserPlayer == null) {
                mDatabaseHelper.getDao(UserPlayer.class).create(userPlayer);
            } else {
                userPlayer.setId(localUserPlayer.getId());
                mDatabaseHelper.getDao(UserPlayer.class).update(userPlayer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UserPlayer getPlayerForGamingGroupId(String selectedGamingGroupServerId) {
        try {
            return mDatabaseHelper.getDao(UserPlayer.class).queryBuilder().where().eq(UserPlayer.COLUMN_GAMING_GROUP_ID, selectedGamingGroupServerId).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
