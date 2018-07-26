package com.nemestats.boardgametracker.viewModels.UIViewModel;

import com.nemestats.boardgametracker.domain.GamingGroup;

/**
 * Created by geomehedeniuc on 5/1/18.
 */

public class GamingGroupViewModel {

    private GamingGroup mGamingGroup;


    int numberOfPlayersInGamingGroup;
    int numberOfGameDefinitionsInGamingGroup;
    int numberOfPlayedGamesInGamingGroup;
    private String mPlayersNames;

    public GamingGroupViewModel(GamingGroup gamingGroup) {
        mGamingGroup = gamingGroup;
    }

    public GamingGroup getGamingGroup() {
        return mGamingGroup;
    }

    public int getNumberOfPlayersInGamingGroup() {
        return numberOfPlayersInGamingGroup;
    }

    public void setNumberOfPlayersInGamingGroup(int numberOfPlayersInGamingGroup) {
        this.numberOfPlayersInGamingGroup = numberOfPlayersInGamingGroup;
    }

    public int getNumberOfGameDefinitionsInGamingGroup() {
        return numberOfGameDefinitionsInGamingGroup;
    }

    public void setNumberOfGameDefinitionsInGamingGroup(int numberOfGameDefinitionsInGamingGroup) {
        this.numberOfGameDefinitionsInGamingGroup = numberOfGameDefinitionsInGamingGroup;
    }

    public int getNumberOfPlayedGamesInGamingGroup() {
        return numberOfPlayedGamesInGamingGroup;
    }

    public void setNumberOfPlayedGamesInGamingGroup(int numberOfPlayedGamesInGamingGroup) {
        this.numberOfPlayedGamesInGamingGroup = numberOfPlayedGamesInGamingGroup;
    }

    public void setPlayersNames(String playersNames) {
        mPlayersNames = playersNames;
    }

    public String getPlayersNames() {
        return mPlayersNames;
    }
}

