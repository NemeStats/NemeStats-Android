package com.nemestats.boardgametracker.viewModels.UIViewModel;

import org.joda.time.DateTime;

/**
 * Created by mehegeo on 10/15/17.
 */

public class PlayedGameHeaderData {
    private int mHeaderPosition;
    private DateTime mHeaderDate;
    private int mTotalNemeStatsPoints;
    private int mTotalPlayedGames;

    public PlayedGameHeaderData() {
    }

    public int getHeaderPosition() {
        return mHeaderPosition;
    }

    public void setHeaderPosition(int headerPosition) {
        mHeaderPosition = headerPosition;
    }

    public DateTime getHeaderDate() {
        return mHeaderDate;
    }

    public void setHeaderDate(DateTime headerDate) {
        mHeaderDate = headerDate;
    }

    public int getTotalNemeStatsPoints() {
        return mTotalNemeStatsPoints;
    }

    public void setTotalNemeStatsPoints(int totalNemeStatsPoints) {
        mTotalNemeStatsPoints = totalNemeStatsPoints;
    }

    public int getTotalPlayedGames() {
        return mTotalPlayedGames;
    }

    public void setTotalPlayedGames(int totalPlayedGames) {
        mTotalPlayedGames = totalPlayedGames;
    }
}
