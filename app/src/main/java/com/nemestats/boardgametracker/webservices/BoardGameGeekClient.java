package com.nemestats.boardgametracker.webservices;

import com.nemestats.boardgametracker.webservices.dto.GameDetailsXMLDTO;
import com.nemestats.boardgametracker.webservices.dto.SearchGameXMLDTO;
import com.nemestats.boardgametracker.webservices.restClient.BoardGameGeekRestClient;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by mehegeo on 10/8/17.
 */

public class BoardGameGeekClient {

    private final Retrofit mRetrofitClient;
    private BoardGameGeekService mBoardGameGeekService;

    @Inject
    public BoardGameGeekClient(BoardGameGeekRestClient boardGameGeekRestClient) {
        mRetrofitClient = new Retrofit.Builder()
                .baseUrl(RestConfig.ENPOINT_BOARD_GAME_GEEK)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(boardGameGeekRestClient.getOkHttpClient())
                .build();
    }

    public BoardGameGeekService getService() {
        if (mBoardGameGeekService == null) {
            mBoardGameGeekService = mRetrofitClient.create(BoardGameGeekService.class);
        }
        return mBoardGameGeekService;
    }

    public interface BoardGameGeekService {
        @GET(value = "boardgame/{boardGameId}")
        Observable<GameDetailsXMLDTO> getThumnailUrlForBoardGameId(@Path("boardGameId") int boardGameId);

        @GET(value = "search")
        Observable<SearchGameXMLDTO> searchForGamesByName(@Query("search") String boardGameName);
    }
}
