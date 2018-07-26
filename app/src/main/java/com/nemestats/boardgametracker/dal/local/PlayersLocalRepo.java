package com.nemestats.boardgametracker.dal.local;

import com.nemestats.boardgametracker.domain.Player;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/6/17.
 */

public class PlayersLocalRepo {
    private DatabaseHelper mDatabaseHelper;

    @Inject
    public PlayersLocalRepo(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }


    public void savePlayers(List<Player> playersList) {
        try {
            TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(), () -> {
                for (Iterator<Player> iterator = playersList.iterator(); iterator.hasNext(); ) {
                    createOrUpdate(iterator.next());
                }
                return null;
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createOrUpdate(Player player) {
        try {
            Player localPlayer = mDatabaseHelper.getDao(Player.class).queryBuilder().where().eq(Player.COLUMN_SERVER_ID, player.getServerId()).queryForFirst();
            if (localPlayer == null) {
                mDatabaseHelper.getDao(Player.class).create(player);
            } else {
                player.setId(localPlayer.getId());
                mDatabaseHelper.getDao(Player.class).update(player);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Player> getPlayersInGamingGroupId(String gamingGroupIdServerId, boolean activeOnly) {
        List<Player> playerList = new ArrayList<>();
        try {
            Where<Player, ?> queryBuilder = mDatabaseHelper.getDao(Player.class).queryBuilder()
                    .orderBy(Player.COLUMN_PLAYER_NAME, true)
                    .where()
                    .eq(Player.COLUMN_GAMING_GROUP_SERVER_ID, gamingGroupIdServerId);
            if (activeOnly) {
                queryBuilder.and().eq(Player.COLUMN_ACTIVE, true);
            }
            playerList.addAll(queryBuilder.query());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playerList;
    }

    public long getNumberOfPlayersInGamingGroup(String gamingGroupId) {
        try {
            return mDatabaseHelper.getDao(Player.class).queryBuilder().where().eq(Player.COLUMN_GAMING_GROUP_SERVER_ID, gamingGroupId).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Player getPlayerById(int id) {
        try {
            return mDatabaseHelper.getDao(Player.class).queryBuilder()
                    .where()
                    .eq(Player.COLUMN_SERVER_ID, id).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
