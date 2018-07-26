package com.nemestats.boardgametracker.webservices.dto.userAccount;

/**
 * Created by mehegeo on 9/23/17.
 */

public class CreateAccountRequestDTO {
    private String emailAddress;
    private String userName;
    private String password;
    private String uniqueDeviceId;

    public CreateAccountRequestDTO(String emailAddress, String userName, String password, String uniqueDeviceId) {
        this.emailAddress = emailAddress;
        this.userName = userName;
        this.password = password;
        this.uniqueDeviceId = uniqueDeviceId;
    }
}
