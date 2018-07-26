package com.nemestats.boardgametracker.webservices.dto.userAccount;

/**
 * Created by mehegeo on 9/23/17.
 */

public class CreateUserSessionResponseDTO {
    private String authenticationToken;
    private String authenticationTokenExpirationDateTime;

    public String getAuthenticationToken() {
        return authenticationToken;
    }

    public String getAuthenticationTokenExpirationDateTime() {
        return authenticationTokenExpirationDateTime;
    }
}
