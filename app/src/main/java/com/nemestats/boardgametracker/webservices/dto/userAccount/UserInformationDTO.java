package com.nemestats.boardgametracker.webservices.dto.userAccount;

import com.nemestats.boardgametracker.webservices.dto.GamingGroupDTO;
import com.nemestats.boardgametracker.webservices.dto.players.UserPlayerDTO;

import java.util.List;

/**
 * Created by mehegeo on 10/5/17.
 */

public class UserInformationDTO {
    private String userId;
    private String userName;
    private String emailAddress;
    private List<GamingGroupDTO> gamingGroups;
    private List<UserPlayerDTO> players;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public List<GamingGroupDTO> getGamingGroups() {
        return gamingGroups;
    }

    public List<UserPlayerDTO> getUserPlayers() {
        return players;
    }
}
