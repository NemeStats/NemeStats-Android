package com.nemestats.boardgametracker.managers;

import com.nemestats.boardgametracker.dal.local.UserPlayersLocalRepo;
import com.nemestats.boardgametracker.domain.UserPlayer;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/15/17.
 */

public class UserPlayerManager {

    private UserPlayersLocalRepo mUserPlayersLocalRepo;

    @Inject
    public UserPlayerManager(UserPlayersLocalRepo userPlayersLocalRepo) {
        mUserPlayersLocalRepo = userPlayersLocalRepo;
    }

    public void createOrUpdate(UserPlayer userPlayer) {
        mUserPlayersLocalRepo.createOrUpdate(userPlayer);
    }


    public UserPlayer getPlayerForGamingGroupId(String selectedGamingGroupServerId) {
        return mUserPlayersLocalRepo.getPlayerForGamingGroupId(selectedGamingGroupServerId);
    }
}
