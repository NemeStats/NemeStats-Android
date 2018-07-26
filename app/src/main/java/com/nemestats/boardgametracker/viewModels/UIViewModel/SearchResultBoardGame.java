package com.nemestats.boardgametracker.viewModels.UIViewModel;

/**
 * Created by geomehedeniuc on 3/25/18.
 */

public class SearchResultBoardGame {
    private String mBoardGameObjectId;
    private String mName;
    private String mYear;

    public SearchResultBoardGame(String boardGameObjectId, String name, String year) {
        mBoardGameObjectId = boardGameObjectId;
        mName = name;
        mYear = year;
    }

    public String getBoardGameObjectId() {
        return mBoardGameObjectId;
    }

    public String getName() {
        return mName;
    }

    public String getYear() {
        return mYear;
    }
}
