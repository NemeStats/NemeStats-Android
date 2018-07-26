package com.nemestats.boardgametracker.webservices.dto.userAccount;

/**
 * Created by mehegeo on 9/23/17.
 */

public class CreateUserSessionRequestDTO {
    private String userName;
    private String password;
    private String uniqueDeviceId;

    public CreateUserSessionRequestDTO(String userName, String password, String uniqueDeviceId) {
        this.userName = userName;
        this.password = password;
        this.uniqueDeviceId = uniqueDeviceId;
    }
}
