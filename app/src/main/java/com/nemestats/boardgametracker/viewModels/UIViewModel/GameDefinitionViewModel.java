package com.nemestats.boardgametracker.viewModels.UIViewModel;

import com.nemestats.boardgametracker.domain.GameDefinition;

/**
 * Created by mehegeo on 10/8/17.
 */

public class GameDefinitionViewModel {
    private GameDefinition mGameDefinition;
    private int mNumberOfPlayedGames;
    private boolean mIsSelected;

    public GameDefinitionViewModel(GameDefinition gameDefinition) {
        mGameDefinition = gameDefinition;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    public void setSelected(boolean selected) {
        mIsSelected = selected;
    }

    public GameDefinition getGameDefinition() {
        return mGameDefinition;
    }

    public int getNumberOfPlayedGames() {
        return mNumberOfPlayedGames;
    }

    public void setNumberOfPlayedGames(int numberOfPlayedGames) {
        mNumberOfPlayedGames = numberOfPlayedGames;
    }

    public void setGameDefinition(GameDefinition gameDefinition) {
        mGameDefinition = gameDefinition;
    }
}
