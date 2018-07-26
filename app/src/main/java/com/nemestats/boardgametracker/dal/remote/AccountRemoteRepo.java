package com.nemestats.boardgametracker.dal.remote;

import com.nemestats.boardgametracker.webservices.NemeStatsServicesClientWihoutAuthentification;
import com.nemestats.boardgametracker.webservices.NemeStatsServicesClientWithAuthentication;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateAccountRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateAccountResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateUserSessionRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateUserSessionResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.UserInformationDTO;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by mehegeo on 9/23/17.
 */

public class AccountRemoteRepo {

    private NemeStatsServicesClientWithAuthentication mNemeStatsServicesClientWithAuthentication;
    private NemeStatsServicesClientWihoutAuthentification mNemeStatsServicesClientWihoutAuthentification;

    @Inject
    public AccountRemoteRepo(NemeStatsServicesClientWihoutAuthentification nemeStatsServicesClientWihoutAuthentification, NemeStatsServicesClientWithAuthentication nemeStatsServicesClientWithAuthentication) {
        mNemeStatsServicesClientWithAuthentication = nemeStatsServicesClientWithAuthentication;
        mNemeStatsServicesClientWihoutAuthentification = nemeStatsServicesClientWihoutAuthentification;
    }

    public Observable<UserInformationDTO> getUserInformation(String userId) {
        if (userId == null || userId.isEmpty()) {
            userId = "null";
        }
        return mNemeStatsServicesClientWithAuthentication.getService().getUserInformation(userId);
    }


    public Observable<CreateUserSessionResponseDTO> createUserSession(String userName, String password) {

        CreateUserSessionRequestDTO createUserSessionRequestDTO = new CreateUserSessionRequestDTO(userName, password, java.util.UUID.randomUUID().toString());
        return mNemeStatsServicesClientWihoutAuthentification.getService().createUserSession(createUserSessionRequestDTO);

    }

    public Observable<CreateAccountResponseDTO> createAccount(String email, String userName, String password, String confirmationPassword) {
        CreateAccountRequestDTO createAccountRequestDTO = new CreateAccountRequestDTO(email, userName, password, null);
        return mNemeStatsServicesClientWihoutAuthentification.getService().createAccount(createAccountRequestDTO);
    }
}
