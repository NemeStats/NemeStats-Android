package com.nemestats.boardgametracker.dal.local;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.webservices.dto.GameDetailsXMLDTO;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/7/17.
 */

public class GameDefinitionLocalRepo {

    private DatabaseHelper mDatabaseHelper;

    @Inject
    public GameDefinitionLocalRepo(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }

    public void savePlayedGamesList(List<GameDefinition> gameDefinitionList) {
        try {
            TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(), () -> {
                for (Iterator<GameDefinition> iterator = gameDefinitionList.iterator(); iterator.hasNext(); ) {
                    createOrUpdate(iterator.next());
                }
                return null;
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createOrUpdate(GameDefinition gameDefinition) {
        try {
            GameDefinition localGameDefinition = getGameDefinitionByBoardGameGeekIdAndGroupId(gameDefinition.getBoardGameGeekObjectId(), gameDefinition.getGamingGroupId());
            if (localGameDefinition == null) {
                mDatabaseHelper.getDao(GameDefinition.class).create(gameDefinition);
            } else {
                if (localGameDefinition.getThumbnailLocalPath() != null) {
                    gameDefinition.setThumbnailLocalPath(localGameDefinition.getThumbnailLocalPath());
                    gameDefinition.setMinPlayers(localGameDefinition.getMinPlayers());
                    gameDefinition.setMaxPlayers(localGameDefinition.getMaxPlayers());
                    gameDefinition.setMinPlayTime(localGameDefinition.getMinPlayTime());
                    gameDefinition.setMaxPlayTime(localGameDefinition.getMaxPlayTime());
                    gameDefinition.setDescription(localGameDefinition.getDescription());
                    gameDefinition.setYearPublished(localGameDefinition.getYearPublished());
                    gameDefinition.setCategories(localGameDefinition.getCategories());
                    gameDefinition.setMechanics(localGameDefinition.getMechanics());
                }

                gameDefinition.setId(localGameDefinition.getId());
                mDatabaseHelper.getDao(GameDefinition.class).update(gameDefinition);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getLocalNumberOfGameDefinitionsInGamingGroup(String gamingGroupServerId) {
        try {
            return mDatabaseHelper.getDao(GameDefinition.class).queryBuilder().where().eq(GameDefinition.COLUMN_GAMING_GROUP_ID, gamingGroupServerId).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<GameDefinition> getGameDefinitionsInGamingGroup(String gamingGroupServerId, boolean activeOnly) {
        List<GameDefinition> gameDefinitionList = new ArrayList<>();
        try {
            Where<GameDefinition, ?> queryBuilder = mDatabaseHelper.getDao(GameDefinition.class).queryBuilder().where().eq(GameDefinition.COLUMN_GAMING_GROUP_ID, gamingGroupServerId);
            if (activeOnly) {
                queryBuilder.and().eq(GameDefinition.COLUMN_ACTIVE, true);
            }
            gameDefinitionList.addAll(queryBuilder.query());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gameDefinitionList;
    }

    public GameDefinition getGameDefinitionByServerId(int serverId) {
        try {
            return mDatabaseHelper.getDao(GameDefinition.class).queryBuilder().where().eq(GameDefinition.COLUMN_SERVER_ID, serverId).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteGameDefinitionByBoardGameGeekId(int boardGameObjectId) {
        DeleteBuilder<GameDefinition, ?> delete = null;
        try {
            delete = mDatabaseHelper.getDao(GameDefinition.class)
                    .deleteBuilder();
            delete.where().eq(GameDefinition.COLUMN_BOARD_GAME_GEEK_OBJECT_ID, boardGameObjectId);
            delete.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GameDefinition getGameDefinitionById(int id) {
        try {
            return mDatabaseHelper.getDao(GameDefinition.class).queryBuilder().where().eq(GameDefinition.COLUMN_ID, id).queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GameDefinition getGameDefinitionByBoardGameGeekIdAndGroupId(int boardGameObjectId, String gamingGroupId) {
        try {
            return mDatabaseHelper.getDao(GameDefinition.class)
                    .queryBuilder()
                    .where()
                    .eq(GameDefinition.COLUMN_BOARD_GAME_GEEK_OBJECT_ID, boardGameObjectId)
                    .and()
                    .eq(GameDefinition.COLUMN_GAMING_GROUP_ID, gamingGroupId)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
