package com.nemestats.boardgametracker.interfaces;

import com.nemestats.boardgametracker.domain.GameDefinition;

/**
 * Created by geomehedeniuc on 4/9/18.
 */

public interface OnDataChangedListener {
    void onGameDefinitionUpdated(GameDefinition gameDefinition);
}
