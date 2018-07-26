package com.nemestats.boardgametracker.webservices.dto.userAccount;

/**
 * Created by mehegeo on 9/23/17.
 */

public class CreateAccountResponseDTO extends CreateUserSessionResponseDTO {

    private String userId;
    private int playerId;
    private String playerName;
    private int gamingGroupId;
    private String gamingGroupName;

    @Override
    public String getAuthenticationToken() {
        return super.getAuthenticationToken();
    }

    @Override
    public String getAuthenticationTokenExpirationDateTime() {
        return super.getAuthenticationTokenExpirationDateTime();
    }

    public String getUserId() {
        return userId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGamingGroupId() {
        return gamingGroupId;
    }

    public String getGamingGroupName() {
        return gamingGroupName;
    }
}
