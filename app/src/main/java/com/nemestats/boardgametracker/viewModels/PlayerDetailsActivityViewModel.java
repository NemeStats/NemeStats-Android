package com.nemestats.boardgametracker.viewModels;

import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.managers.PlayersManager;

import javax.inject.Inject;

/**
 * Created by geomehedeniuc on 5/19/18.
 */

public class PlayerDetailsActivityViewModel {

    private PlayersManager mPlayersManager;

    private Player mPlayer;
    private boolean mPlayerEdited;

    @Inject
    public PlayerDetailsActivityViewModel(PlayersManager playersManager) {
        mPlayersManager = playersManager;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void setPlayer(Player player) {
        mPlayer = player;
    }

    public void setPlayerEdited(boolean playerEdited) {
        mPlayerEdited = playerEdited;
    }

    public boolean isPlayerEdited() {
        return mPlayerEdited;
    }
}
