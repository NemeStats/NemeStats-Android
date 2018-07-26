package com.nemestats.boardgametracker.webservices.dto;

/**
 * Created by mehegeo on 10/5/17.
 */

public class GamingGroupDTO {

    private String gamingGroupId;
    private String gamingGroupName;
    private String gamingGroupPublicDescription;
    private String gamingGroupPublicUrl;
    private String nemeStatsUrl;
    private boolean active;

    public String getNemeStatsUrl() {
        return nemeStatsUrl;
    }

    public boolean isActive() {
        return active;
    }

    public String getGamingGroupId() {
        return gamingGroupId;
    }

    public String getGamingGroupName() {
        return gamingGroupName;
    }

    public String getGamingGroupPublicDescription() {
        return gamingGroupPublicDescription;
    }

    public String getGamingGroupPublicUrl() {
        return gamingGroupPublicUrl;
    }
}
