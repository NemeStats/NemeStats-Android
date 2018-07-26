package com.nemestats.boardgametracker.dal.remote;

import com.nemestats.boardgametracker.webservices.BoardGameGeekClient;
import com.nemestats.boardgametracker.webservices.dto.GameDetailsXMLDTO;
import com.nemestats.boardgametracker.webservices.dto.SearchGameXMLDTO;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/8/17.
 */

public class BoardGameGeekRemoteRepo {

    private BoardGameGeekClient mBoardGameGeekClient;

    @Inject
    public BoardGameGeekRemoteRepo(BoardGameGeekClient boardGameGeekClient) {
        mBoardGameGeekClient = boardGameGeekClient;
    }

    public Observable<GameDetailsXMLDTO> getThumbnailPathForBBGObjectId(int boardGameGeekObjectId) {
        return mBoardGameGeekClient.getService().getThumnailUrlForBoardGameId(boardGameGeekObjectId);
    }

    public Observable<SearchGameXMLDTO> searchForGamesByName(String boardGameName) {
        return mBoardGameGeekClient.getService().searchForGamesByName(boardGameName);
    }
}
