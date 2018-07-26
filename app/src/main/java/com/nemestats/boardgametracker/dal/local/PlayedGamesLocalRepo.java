package com.nemestats.boardgametracker.dal.local;

import com.nemestats.boardgametracker.domain.ApplicationLinkage;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/7/17.
 */

public class PlayedGamesLocalRepo {

    private DatabaseHelper mDatabaseHelper;

    @Inject
    public PlayedGamesLocalRepo(DatabaseHelper databaseHelper) {
        mDatabaseHelper = databaseHelper;
    }


    public void savePlayedGamesList(List<PlayedGame> playedGameList) {
        try {
            TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(), () -> {
                for (Iterator<PlayedGame> iterator = playedGameList.iterator(); iterator.hasNext(); ) {
                    createOrUpdate(iterator.next());
                }
                return null;
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createOrUpdate(PlayedGame playedGame) {
        try {
            PlayedGame localPlayedGame = mDatabaseHelper.getDao(PlayedGame.class).queryBuilder().where().eq(Player.COLUMN_SERVER_ID, playedGame.getServerId()).queryForFirst();
            if (localPlayedGame == null) {
                mDatabaseHelper.getDao(PlayedGame.class).create(playedGame);
            } else {
                playedGame.setId(localPlayedGame.getId());
                mDatabaseHelper.getDao(PlayedGame.class).update(playedGame);
            }

            deletePlayerGameResultsForPlayedGame(playedGame);
            deleteApplicationLinkageForPlayedGame(playedGame);

            List<PlayerGameResults> playerGameResultsList = playedGame.getPlayerGameResultsList();
            for (Iterator<PlayerGameResults> iterator = playerGameResultsList.iterator(); iterator.hasNext(); ) {
                savePlayerGameResult(iterator.next());
            }

            List<ApplicationLinkage> applicationLinkageList = playedGame.getApplicationLinkagesList();
            for (Iterator<ApplicationLinkage> iterator = applicationLinkageList.iterator(); iterator.hasNext(); ) {
                saveApplicationLinkage(iterator.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteApplicationLinkageForPlayedGame(PlayedGame playedGame) {
        DeleteBuilder<ApplicationLinkage, ?> delete = null;
        try {
            delete = mDatabaseHelper.getDao(ApplicationLinkage.class)
                    .deleteBuilder();
            delete.where().eq(ApplicationLinkage.COLUMN_PARENT_PLAYED_GAME, playedGame.getId());
            delete.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void deletePlayerGameResultsForPlayedGame(PlayedGame playedGame) {
        DeleteBuilder<PlayerGameResults, ?> delete = null;
        try {
            delete = mDatabaseHelper.getDao(PlayerGameResults.class)
                    .deleteBuilder();
            delete.where().eq(PlayerGameResults.COLUMN_PARENT_PLAYED_GAME, playedGame.getId());
            delete.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void saveApplicationLinkage(ApplicationLinkage applicationLinkage) {
        DeleteBuilder<ApplicationLinkage, ?> delete = null;
        try {
            delete = mDatabaseHelper.getDao(ApplicationLinkage.class)
                    .deleteBuilder();
            delete.where().eq(ApplicationLinkage.COLUMN_ENTITY_ID, applicationLinkage.getEntityId());
            delete.delete();

            mDatabaseHelper.getDao(ApplicationLinkage.class).create(applicationLinkage);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void savePlayerGameResult(PlayerGameResults playerGameResults) {
        try {
            mDatabaseHelper.getDao(PlayerGameResults.class).create(playerGameResults);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PlayedGame> getAll() {
        List<PlayedGame> playedGameList = new ArrayList<>();
        try {
            playedGameList.addAll(mDatabaseHelper.getDao(PlayedGame.class).queryForAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playedGameList;
    }

    public long getLocalNumberOfPlayedGamesInGamingGroup(GamingGroup gamingGroup) {
        try {
            return mDatabaseHelper.getDao(PlayedGame.class).queryBuilder().where().eq(PlayedGame.COLUMN_GAMING_GROUP_ID, gamingGroup.getServerId()).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getLocalNumberOfPlayedGamesInGamingGroupForGameDefinition(String selectedGamingGroupServerId, int gameDefinitionServerId) {
        try {
            return mDatabaseHelper.getDao(PlayedGame.class).queryBuilder().where().eq(PlayedGame.COLUMN_GAMING_GROUP_ID, selectedGamingGroupServerId).and().eq(PlayedGame.COLUMN_GAME_DEFINITION_ID, gameDefinitionServerId).countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<PlayedGame> getSortedPlayedGamesList(String selectedGamingGroupServerId, long numberOfRecors) {
        List<PlayedGame> playedGameList = new ArrayList<>();
        try {
            playedGameList.addAll(
                    mDatabaseHelper.getDao(PlayedGame.class).queryBuilder()
                            .limit(numberOfRecors)
                            .orderBy(PlayedGame.COLUMN_DATE_PLAYED, false)
                            .where().eq(PlayedGame.COLUMN_GAMING_GROUP_ID, selectedGamingGroupServerId)
                            .query());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playedGameList;
    }

    public void updateGameDefinitionInPlayedGames(int gameDefinitionServerId, String gameDefinitionName) {
        try {
            UpdateBuilder<PlayedGame, ?> updateBuilder = mDatabaseHelper.getDao(PlayedGame.class).updateBuilder();
            updateBuilder.where().eq(PlayedGame.COLUMN_GAME_DEFINITION_ID, gameDefinitionServerId);
            updateBuilder.updateColumnValue(PlayedGame.COLUMN_GAME_DEFINITION_NAME, gameDefinitionName);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerNameInPlayedGames(Player player) {
        try {
            TransactionManager.callInTransaction(mDatabaseHelper.getConnectionSource(), () -> {
                UpdateBuilder<PlayerGameResults, ?> updateBuilder = mDatabaseHelper.getDao(PlayerGameResults.class).updateBuilder();
                updateBuilder.where().eq(PlayerGameResults.COLUMN_PLAYER_SERVER_ID, player.getServerId());
                updateBuilder.updateColumnValue(PlayerGameResults.COLUMN_PLAYER_NAME, player.getPlayerName());
                updateBuilder.updateColumnValue(PlayerGameResults.COLUMN_PLAYER_ACTIVE, player.isActive());
                updateBuilder.update();
                return null;
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
