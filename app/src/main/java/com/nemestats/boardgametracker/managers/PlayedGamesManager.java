package com.nemestats.boardgametracker.managers;

import com.nemestats.boardgametracker.dal.local.PlayedGamesLocalRepo;
import com.nemestats.boardgametracker.dal.remote.PlayedGamesRemoteRepo;
import com.nemestats.boardgametracker.domain.ApplicationLinkage;
import com.nemestats.boardgametracker.domain.GameDefinition;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.PlayedGame;
import com.nemestats.boardgametracker.domain.Player;
import com.nemestats.boardgametracker.domain.PlayerGameResults;
import com.nemestats.boardgametracker.utils.PlayedGameUtils;
import com.nemestats.boardgametracker.webservices.dto.playedGames.ApplicationLinkageDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.CreateNewGameResultResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.playedGames.PlayedGamesDTO;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by mehegeo on 10/7/17.
 */

public class PlayedGamesManager {

    private PlayedGamesRemoteRepo mPlayedGamesRemoteRepo;
    private PlayedGamesLocalRepo mPlayedGamesLocalRepo;
    private AccountManager mAccountManager;

    @Inject
    public PlayedGamesManager(PlayedGamesRemoteRepo playedGamesRemoteRepo, PlayedGamesLocalRepo playedGamesLocalRepo, AccountManager accountManager) {
        mPlayedGamesRemoteRepo = playedGamesRemoteRepo;
        mPlayedGamesLocalRepo = playedGamesLocalRepo;
        mAccountManager = accountManager;
    }

    public Observable<List<PlayedGame>> getPlayedGamesForGamingGroup(String gamingGroup, long lastSyncDate) {
        DateTime dateTime = new DateTime(lastSyncDate);
        dateTime = dateTime.minusDays(1);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        String startDateGameLastUpdatedString = formatter.print(dateTime);

        return mPlayedGamesRemoteRepo.getPlayedGamesForGamingGroup(gamingGroup, startDateGameLastUpdatedString)
                .retryWhen(mAccountManager.getRefreshUserSessionFunction())
                .map(playedGamesDTO -> {
                    List<PlayedGame> playedGamesList = new ArrayList<>();
                    for (PlayedGamesDTO.PlayedGameDTO playedGameDTO : playedGamesDTO.getPlayedGames()) {
                        String datePlayedString = playedGameDTO.getDatePlayed();
                        String dateLastUpdatedString = playedGameDTO.getDateLastUpdated();


                        DateTime datePlayed = datePlayedString != null ? formatter.parseDateTime(datePlayedString) : null;
                        DateTime dateLastUpdated = dateLastUpdatedString != null ? formatter.parseDateTime(dateLastUpdatedString) : null;

                        PlayedGame playedGame = new PlayedGame(
                                playedGameDTO.getPlayedGameId(),
                                playedGameDTO.getGameDefinitionId(),
                                playedGameDTO.getGameDefinitionName(),
                                playedGameDTO.getBoardGameGeekDefinitionId(),
                                playedGameDTO.getGamingGroupId(),
                                playedGameDTO.getGamingGroupName(),
                                playedGameDTO.getNotes(),
                                datePlayed,
                                dateLastUpdated,
                                playedGameDTO.getWinnerType(),
                                playedGameDTO.getNemeStatsUrl()
                        );

                        List<PlayerGameResults> playerGameResultsList = new ArrayList<>();
                        for (PlayedGamesDTO.PlayedGameDTO.PlayerGameResultDTO playedGameResultDTO : playedGameDTO.getPlayerGameResults()) {
                            PlayerGameResults playerGameResult = new PlayerGameResults(
                                    playedGameResultDTO.getPlayerId(),
                                    playedGameResultDTO.getPlayerName(),
                                    playedGameResultDTO.isPlayerActive(),
                                    playedGameResultDTO.getGameRank(),
                                    playedGameResultDTO.getPointsScored(),
                                    playedGameResultDTO.getNemeStatsPointsAwarded(),
                                    playedGameResultDTO.getGameWeightBonusNemePoints(),
                                    playedGameResultDTO.getGameDurationBonusNemePoints(),
                                    playedGameResultDTO.getTotalNemeStatsPointsAwarded()
                            );

                            playerGameResult.setPlayedGame(playedGame);
                            playerGameResultsList.add(playerGameResult);
                        }

                        String gameResultType = PlayedGameUtils.getGameTypeFromResults(playerGameResultsList);
                        playedGame.setGameResultType(gameResultType);

                        List<ApplicationLinkage> applicationLinkageList = new ArrayList<>();
                        for (ApplicationLinkageDTO applicationLinkageDTO : playedGameDTO.getApplicationLinkages()) {

                            ApplicationLinkage applicationLinkage = new ApplicationLinkage(applicationLinkageDTO.getApplicationName(), applicationLinkageDTO.getEntityId());
                            applicationLinkage.setPlayedGame(playedGame);
                            applicationLinkageList.add(applicationLinkage);
                        }

                        playedGame.getPlayerGameResultsList().addAll(playerGameResultsList);
                        playedGame.getApplicationLinkagesList().addAll(applicationLinkageList);
                        playedGamesList.add(playedGame);
                    }
                    return playedGamesList;
                });
    }


    public void save(List<PlayedGame> playedGamesList) {
        mPlayedGamesLocalRepo.savePlayedGamesList(playedGamesList);
    }


    public long getLocalNumberOfPlayedGamesInGamingGroup(GamingGroup gamingGroup) {
        return mPlayedGamesLocalRepo.getLocalNumberOfPlayedGamesInGamingGroup(gamingGroup);
    }

    public int getLocalNumberOfPlayedGamesInGamingGroupForGameDefinition(String selectedGamingGroupServerId, int gameDefinitionServerId) {
        return (int) mPlayedGamesLocalRepo.getLocalNumberOfPlayedGamesInGamingGroupForGameDefinition(selectedGamingGroupServerId, gameDefinitionServerId);
    }

    public List<PlayedGame> getSortedPlayedGamesList(String selectedGamingGroupServerId, long numberOfRecors) {
        return mPlayedGamesLocalRepo.getSortedPlayedGamesList(selectedGamingGroupServerId, numberOfRecors);
    }

    public Observable<CreateNewGameResultResponseDTO> saveRemoteGame(PlayedGame playedGame) {
        return mPlayedGamesRemoteRepo.saveRemoteGame(playedGame).retryWhen(mAccountManager.getRefreshUserSessionFunction());
    }

    public void updateGameDefinitionInPlayedGames(GameDefinition gameDefinition) {
        mPlayedGamesLocalRepo.updateGameDefinitionInPlayedGames(gameDefinition.getServerId(), gameDefinition.getGameDefinitionName());
    }

    public void updatePlayerNameInPlayedGames(Player player) {
        mPlayedGamesLocalRepo.updatePlayerNameInPlayedGames(player);
    }
}
