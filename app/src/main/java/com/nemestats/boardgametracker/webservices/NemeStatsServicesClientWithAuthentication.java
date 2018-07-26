package com.nemestats.boardgametracker.webservices;

import com.nemestats.boardgametracker.webservices.dto.gameDefinition.UpdateGameDefinitionRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.players.CreatePlayerRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.players.PlayerResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.players.UpdatePlayerRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.CreateGameDefinitionResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.CreateNewGameResultRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.gameDefinition.GameDefinitionsDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.CreateNewGameResultResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.PlayedGamesDTO;
import com.nemestats.boardgametracker.webservices.dto.players.PlayersDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.UserInformationDTO;
import com.nemestats.boardgametracker.webservices.restClient.NemeStatsRestClientWithAuthentification;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.DATE_PLAYED_FROM;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.DATE_PLAYED_TO;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.END_DATE_GAME_LAST_UPDATED;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.EXCLUSION_APPLICATION_NAME;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.GAMING_GROUP_ID;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.INCLUSION_APPLICATION_NAME;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.MAXIMUM_NUMBER_OF_RESULTS;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.PLAYER_ID;
import static com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo.START_DATE_GAME_LAST_UPDATED;

/**
 * Created by mehegeo on 9/23/17.
 */

public class NemeStatsServicesClientWithAuthentication {

    private Retrofit mRetrofitClient;
    private NemeStatsService mNemeStatsService;

    @Inject
    public NemeStatsServicesClientWithAuthentication(NemeStatsRestClientWithAuthentification nemeStatsRestClient) {
        mRetrofitClient = new Retrofit.Builder()
                .baseUrl(RestConfig.ENPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(nemeStatsRestClient.getOkHttpClient())
                .build();
    }

    public NemeStatsService getService() {
        if (mNemeStatsService == null) {
            mNemeStatsService = mRetrofitClient.create(NemeStatsService.class);
        }
        return mNemeStatsService;
    }

    public interface NemeStatsService {

        @GET(value = "Users/{userId}")
        Observable<UserInformationDTO> getUserInformation(@Path("userId") String userId);

        @GET(value = "Players")
        Observable<PlayersDTO> getPlayersInGamingGroup(@Query("gamingGroupId") String gamingGroupId);

        @POST(value = "Players")
        Observable<PlayerResponseDTO> savePlayer(@Body CreatePlayerRequestDTO createPlayerRequestDTO);

        @PUT(value = "Players/{playerId}")
        Observable<Void> updatePlayer(@Body UpdatePlayerRequestDTO updatePlayerRequestDTO, @Path("playerId") String playerId);

        @GET(value = "PlayedGames")
        Observable<PlayedGamesDTO> getPlayedGamesForGamingGroup(
                @Query(GAMING_GROUP_ID) String gamingGroupId,
                @Query(START_DATE_GAME_LAST_UPDATED) String startDateGameLastUpdated,
                @Query(END_DATE_GAME_LAST_UPDATED) String endDateGameLastUpdated,
                @Query(DATE_PLAYED_FROM) String datePlayedFrom,
                @Query(DATE_PLAYED_TO) String datePlayedTo,
                @Query(PLAYER_ID) String playerId,
                @Query(EXCLUSION_APPLICATION_NAME) String exclusionApplicationName,
                @Query(INCLUSION_APPLICATION_NAME) String inclusionApplicationName,
                @Query(MAXIMUM_NUMBER_OF_RESULTS) String maximumNumberOfResults);

        @GET(value = "GameDefinitions")
        Observable<GameDefinitionsDTO> getGameDefinitionsForGamingGroup(@Query("gamingGroupId") String gamingGroupId);

        @POST(value = "GameDefinitions")
        Observable<CreateGameDefinitionResponseDTO> createGameDefinition(@Body CreateGameDefinitionRequestDTO createGameDefinitionRequestDTO);

        @PUT(value = "GameDefinitions/{gameDefinitionId}")
        Observable<Void> updateGameDefinition(@Body UpdateGameDefinitionRequestDTO updateGameDefinitionRequestDTO, @Path("gameDefinitionId") String s);

        @POST(value = "PlayedGames")
        Observable<CreateNewGameResultResponseDTO> savePlayedGame(@Body CreateNewGameResultRequestDTO createNewGameResultRequestDTO);


    }

}
