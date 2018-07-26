package com.nemestats.boardgametracker.dal.remote;

import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.nemestats.boardgametracker.webservices.NemeStatsServicesClientWithAuthentication;
import com.nemestats.boardgametracker.webservices.dto.playedGames.ApplicationLinkageDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.CreateNewGameResultRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.CreateNewGameResultResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.PlayedGamesDTO;

import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/7/17.
 */

public class PlayedGamesRemoteRepo {

    public static final String GAMING_GROUP_ID = "gamingGroupId";
    public static final String START_DATE_GAME_LAST_UPDATED = "startDateGameLastUpdated";
    public static final String END_DATE_GAME_LAST_UPDATED = "endDateGameLastUpdated";
    public static final String DATE_PLAYED_FROM = "datePlayedFrom";
    public static final String DATE_PLAYED_TO = "datePlayedTo";
    public static final String PLAYER_ID = "playerId";
    public static final String EXCLUSION_APPLICATION_NAME = "exclusionApplicationName";
    public static final String INCLUSION_APPLICATION_NAME = "inclusionApplicationName";
    public static final String MAXIMUM_NUMBER_OF_RESULTS = "maximumNumberOfResults";

    public static final String APPLICATION_LINKAGE_NAME = "nemestats-android";

    private NemeStatsServicesClientWithAuthentication mNemeStatsServicesClientWithAuthentication;

    @Inject
    public PlayedGamesRemoteRepo(NemeStatsServicesClientWithAuthentication nemeStatsServicesClientWithAuthentication) {
        mNemeStatsServicesClientWithAuthentication = nemeStatsServicesClientWithAuthentication;
    }

    public Observable<PlayedGamesDTO> getPlayedGamesForGamingGroup(String gamingGroupId, String startDateGameLastUpdatedString) {
        return mNemeStatsServicesClientWithAuthentication
                .getService()
                .getPlayedGamesForGamingGroup(
                        gamingGroupId,
                        startDateGameLastUpdatedString,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );
    }

    public Observable<CreateNewGameResultResponseDTO> saveRemoteGame(PlayedGame playedGame) {
        CreateNewGameResultRequestDTO newGameDto = new CreateNewGameResultRequestDTO();
        newGameDto.setDatePlayed(DateTimeFormat.forPattern("yyyy-MM-dd").print(playedGame.getDatePlayed()));
        newGameDto.setGameDefinitionId(playedGame.getGameDefinitionId());
        newGameDto.setGamingGroupId(Integer.parseInt(playedGame.getGamingGroupId()));
        newGameDto.setNotes(playedGame.getNotes());
        newGameDto.setPlayerRanks(new ArrayList<>());
        ApplicationLinkageDTO applicationLinkageDTO = new ApplicationLinkageDTO(APPLICATION_LINKAGE_NAME, UUID.randomUUID().toString());
        newGameDto.setApplicationLinkages(Collections.singletonList(applicationLinkageDTO));


        if (playedGame.getPlayerGameResultsList() != null) {
            for (PlayerGameResults playerGameResult : playedGame.getPlayerGameResultsList()) {
                CreateNewGameResultRequestDTO.PlayerRanks playerRank = new CreateNewGameResultRequestDTO.PlayerRanks();
                playerRank.setGameRank(playerGameResult.getGameRank());
                playerRank.setPlayerId(playerGameResult.getPlayerId());
                playerRank.setPointsScored(playerGameResult.getPointsScored());

                newGameDto.getPlayerRanks().add(playerRank);
            }
        }
        return mNemeStatsServicesClientWithAuthentication.getService().savePlayedGame(newGameDto);
    }
}