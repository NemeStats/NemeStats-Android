package com.nemestats.boardgametracker.managers;

import com.nemestats.boardgametracker.dal.local.GamingGroupLocalRepo;
import com.nemestats.boardgametracker.domain.GamingGroup;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by mehegeo on 10/5/17.
 */

public class GamingGroupsManager {

    private GamingGroupLocalRepo mGamingGroupLocalRepo;

    @Inject
    public GamingGroupsManager(GamingGroupLocalRepo gamingGroupLocalRepo) {
        mGamingGroupLocalRepo = gamingGroupLocalRepo;
    }

    public void createOrUpdate(GamingGroup gamingGroup) {
        mGamingGroupLocalRepo.createOrUpdate(gamingGroup);
    }

    public void delete(GamingGroup gamingGroup) {
        mGamingGroupLocalRepo.delete(gamingGroup);
    }

    public List<GamingGroup> getAll(boolean activeOnly) {
        return mGamingGroupLocalRepo.getAll(activeOnly);
    }

    public GamingGroup getGamingGroupByServerId(String serverId) {
        return mGamingGroupLocalRepo.getGamingGroupByServerId(serverId);
    }
}