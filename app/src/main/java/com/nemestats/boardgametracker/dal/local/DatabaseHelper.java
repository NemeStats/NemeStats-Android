package com.nemestats.boardgametracker.dal.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nemestats.boardgametracker.domain.ApplicationLinkage;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.nemestats.boardgametracker.domain.UserPlayer;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/5/17.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "nemestats.sqlite";

    private static final int DATABASE_VERSION = 1;


    @Inject
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, GamingGroup.class);
            TableUtils.createTable(connectionSource, Player.class);
            TableUtils.createTable(connectionSource, UserPlayer.class);
            TableUtils.createTable(connectionSource, PlayedGame.class);
            TableUtils.createTable(connectionSource, ApplicationLinkage.class);
            TableUtils.createTable(connectionSource, PlayerGameResults.class);
            TableUtils.createTable(connectionSource, GameDefinition.class);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public void dropCreateDatabase() {
        try {
            TableUtils.dropTable(getConnectionSource(), GamingGroup.class, true);
            TableUtils.dropTable(getConnectionSource(), Player.class, true);
            TableUtils.dropTable(getConnectionSource(), PlayedGame.class, true);
            TableUtils.dropTable(getConnectionSource(), ApplicationLinkage.class, true);
            TableUtils.dropTable(getConnectionSource(), PlayerGameResults.class, true);
            TableUtils.dropTable(getConnectionSource(), GameDefinition.class, true);
            TableUtils.dropTable(getConnectionSource(), UserPlayer.class, true);
            onCreate(getWritableDatabase(), getConnectionSource());
        } catch (SQLException e) {
        }
    }
}
