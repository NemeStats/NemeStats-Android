package com.nemestats.boardgametracker.viewModels;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.managers.GameDefinitionManager;
import com.nemestats.boardgametracker.managers.PlayedGamesManager;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/11/17.
 */

public class GameDefinitionDetailsActivityViewModel {

    private GameDefinitionManager mGameDefinitionManager;
    private GameDefinition mGameDefinition;
    private PlayedGamesManager mPlayedGamesManager;
    private boolean mGameDefinitionEdited;

    @Inject
    public GameDefinitionDetailsActivityViewModel(GameDefinitionManager gameDefinitionManager) {
        mGameDefinitionManager = gameDefinitionManager;
    }

    public GameDefinition getGameDefinitionByServerId(int id) {
        mGameDefinition = mGameDefinitionManager.getGameDefinitionByServerId(id);
        return mGameDefinition;
    }

    public GameDefinition getGameDefinition() {
        return mGameDefinition;
    }

    public GameDefinition getGameDefinitionByLocalId(int intExtra) {
        mGameDefinition = mGameDefinitionManager.getGameDefinitionById(intExtra);
        return mGameDefinition;
    }

    public void setGameDefinitionEdited(boolean gameDefinitionEdited) {
        mGameDefinitionEdited = gameDefinitionEdited;
    }

    public boolean isGameDefinitionEdited() {
        return mGameDefinitionEdited;
    }

    public void setGameDefinition(GameDefinition gameDefinition) {
        mGameDefinition = gameDefinition;
    }
}
