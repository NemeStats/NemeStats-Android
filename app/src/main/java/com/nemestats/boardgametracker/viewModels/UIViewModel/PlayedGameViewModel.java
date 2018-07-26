package com.nemestats.boardgametracker.viewModels.UIViewModel;

import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.PlayedGame;

/**
 * Created by mehegeo on 10/12/17.
 */

public class PlayedGameViewModel {

    private PlayedGame mPlayedGame;
    private GameDefinition mGameDefinition;
    private int mEarnedPoints;
    private int mAwardedPosition;

    public PlayedGameViewModel(PlayedGame playedGame) {
        mPlayedGame = playedGame;
    }

    public GameDefinition getGameDefinition() {
        return mGameDefinition;
    }

    public void setGameDefinition(GameDefinition gameDefinition) {
        mGameDefinition = gameDefinition;
    }

    public int getAwardedPosition() {
        return mAwardedPosition;
    }

    public void setAwardedPosition(int awardedPosition) {
        mAwardedPosition = awardedPosition;
    }

    public PlayedGame getPlayedGame() {
        return mPlayedGame;
    }

    public int getEarnedPoints() {
        return mEarnedPoints;
    }

    public void setEarnedPoints(int earnedPoints) {
        mEarnedPoints = earnedPoints;
    }

    public void setPlayedGame(PlayedGame playedGame) {
        mPlayedGame = playedGame;
    }
}
