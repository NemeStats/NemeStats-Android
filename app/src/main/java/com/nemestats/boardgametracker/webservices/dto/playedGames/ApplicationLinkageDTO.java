package com.nemestats.boardgametracker.webservices.dto.playedGames;

public class ApplicationLinkageDTO {
    private String applicationName;
    private String entityId;

    public ApplicationLinkageDTO(String applicationName, String entityId) {
        this.applicationName = applicationName;
        this.entityId = entityId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getEntityId() {
        return entityId;
    }
}