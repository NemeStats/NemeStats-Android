package com.nemestats.boardgametracker.managers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.nemestats.boardgametracker.broadcasts.UserSessionExpiredBroadcastReceiver;
import com.nemestats.boardgametracker.dal.local.AccountLocalRepo;
import com.nemestats.boardgametracker.dal.local.DatabaseHelper;
import com.nemestats.boardgametracker.dal.remote.AccountRemoteRepo;
import com.nemestats.boardgametracker.domain.GamingGroup;
import com.nemestats.boardgametracker.domain.UserPlayer;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateAccountResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateUserSessionResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.GamingGroupDTO;
import com.nemestats.boardgametracker.webservices.dto.players.UserPlayerDTO;

import java.io.File;
import java.net.HttpURLConnection;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

/**
 * Created by mehegeo on 9/23/17.
 */

public class AccountManager {

    private Context mContext;
    private AccountRemoteRepo mAccountRemoteRepo;
    private AccountLocalRepo mAccountLocalRepo;
    private GamingGroupsManager mGamingGroupsManager;
    private UserPlayerManager mUserPlayerManager;

    private DatabaseHelper mDatabaseHelper;
    private Function<Observable<Throwable>, ObservableSource<?>> mRefreshUserSessionFunction;

    @Inject
    public AccountManager(Context context,
                          AccountRemoteRepo accountRemoteRepo,
                          AccountLocalRepo accountLocalRepo,
                          GamingGroupsManager gamingGroupsManager,
                          UserPlayerManager userPlayerManager,
                          DatabaseHelper databaseHelper) {
        mContext = context;
        mGamingGroupsManager = gamingGroupsManager;
        mAccountRemoteRepo = accountRemoteRepo;
        mAccountLocalRepo = accountLocalRepo;
        mUserPlayerManager = userPlayerManager;
        mDatabaseHelper = databaseHelper;
    }

    public Function<Observable<Throwable>, ObservableSource<?>> getRefreshUserSessionFunction() {
        if (mRefreshUserSessionFunction == null) {
            mRefreshUserSessionFunction = throwableObservable -> throwableObservable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
                if (throwable instanceof HttpException) {
                    if (((HttpException) throwable).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        return refreshUserSession();
                    }
                }
                return Observable.error(throwable);
            });
        }
        return mRefreshUserSessionFunction;
    }

    public Observable<?> refreshUserSession() {
        return createUserSession(mAccountLocalRepo.getUserName(), mAccountLocalRepo.getUserPassword())
                .doOnError((Consumer<Throwable>) throwable -> {
                    deleteAllUserData();
                    Intent sessionExpiredIntent = new Intent(UserSessionExpiredBroadcastReceiver.USER_SESSION_EXPIRED);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(sessionExpiredIntent);
                });
    }

    public void deleteAllUserData() {
        Log.e("TEST", "deleteAllUserData: " + System.currentTimeMillis());
        File dir = new File(mContext.getFilesDir().getAbsolutePath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        mDatabaseHelper.dropCreateDatabase();
        mAccountLocalRepo.deleteAll();
        Log.e("TEST", "COMPLETED: " + System.currentTimeMillis());
    }

    public Observable<?> createUserSession(String userName, String password) {
        return mAccountRemoteRepo.createUserSession(userName, password)
                .map((Function<CreateUserSessionResponseDTO, Object>) createUserSessionResponseDTO -> {
                    if (createUserSessionResponseDTO != null) {
                        saveUserName(userName);
                        mAccountLocalRepo.saveUserPassword(password);
                        saveUserCredentials(createUserSessionResponseDTO.getAuthenticationToken(), createUserSessionResponseDTO.getAuthenticationTokenExpirationDateTime());
                    }
                    return createUserSessionResponseDTO;
                });
    }


    public Observable getUserInformation() {
        return mAccountRemoteRepo.getUserInformation(mAccountLocalRepo.getUserId())
                .retryWhen(getRefreshUserSessionFunction())
                .map(userInformationDTO -> {
                    mAccountLocalRepo.saveUserId(userInformationDTO.getUserId());
                    mAccountLocalRepo.saveUsername(userInformationDTO.getUserName());
                    mAccountLocalRepo.saveEmail(userInformationDTO.getEmailAddress());

                    if (userInformationDTO.getGamingGroups() != null && !userInformationDTO.getGamingGroups().isEmpty()) {
                        if (mAccountLocalRepo.getSelectedGamingGroupServerId() == null) {
                            mAccountLocalRepo.setSelectedGamingGroupId(String.valueOf(userInformationDTO.getGamingGroups().get(0).getGamingGroupId()));
                        }
                    }

                    for (GamingGroupDTO gamingGroupDTO : userInformationDTO.getGamingGroups()) {
                        GamingGroup gamingGroup = new GamingGroup(
                                gamingGroupDTO.getGamingGroupId(),
                                gamingGroupDTO.getGamingGroupName(),
                                gamingGroupDTO.getGamingGroupPublicDescription(),
                                gamingGroupDTO.isActive(),
                                gamingGroupDTO.getNemeStatsUrl()
                        );
                        mGamingGroupsManager.createOrUpdate(gamingGroup);
                    }

                    for (UserPlayerDTO player : userInformationDTO.getUserPlayers()) {
                        UserPlayer userPlayer = new UserPlayer(player.getPlayerId(), player.getPlayerName(), player.getGamingGroupId());
                        mUserPlayerManager.createOrUpdate(userPlayer);
                    }
                    return userInformationDTO;
                });
    }

    private void saveUserCredentials(String authenticationToken, String authenticationTokenExpirationDateTime) {
        mAccountLocalRepo.saveAuthenticationToken(authenticationToken);
        mAccountLocalRepo.saveAuthenticationTokenExpirationDate(authenticationTokenExpirationDateTime);
    }

    public boolean isUserLoggedIn() {
        return mAccountLocalRepo.isUserLogged();
    }

    public void saveUserName(String userName) {
        mAccountLocalRepo.saveUsername(userName);
    }

    public void saveUserInformation(String userId, int playerId, String playerName, int gamingGroupId, String gamingGroupName) {
        mAccountLocalRepo.saveUserId(userId);
        mAccountLocalRepo.savePlayerId(playerId);
        mAccountLocalRepo.savePlayerName(playerName);
        mAccountLocalRepo.saveGamingGroupId(gamingGroupId);
        mAccountLocalRepo.saveGamingGroupName(gamingGroupName);
    }

    public void saveUserInformation(String userName, String email) {
        saveUserName(userName);
        mAccountLocalRepo.saveEmail(email);
    }

    public Completable createAccount(String email, String userName, String password, String confirmationPassword) {
        return Completable.fromObservable(
                mAccountRemoteRepo
                        .createAccount(email, userName, password, confirmationPassword)
                        .map((Function<CreateAccountResponseDTO, Object>) createAccountResponseDTO -> {

                                    saveUserInformation(userName, email);
                                    saveUserCredentials(createAccountResponseDTO.getAuthenticationToken(), createAccountResponseDTO.getAuthenticationTokenExpirationDateTime());

                                    saveUserInformation(
                                            createAccountResponseDTO.getUserId(),
                                            createAccountResponseDTO.getPlayerId(),
                                            createAccountResponseDTO.getPlayerName(),
                                            createAccountResponseDTO.getGamingGroupId(),
                                            createAccountResponseDTO.getGamingGroupName()
                                    );

                                    return createAccountResponseDTO;
                                }
                        ));
    }

    public void setSelectedGamingGroup(GamingGroup gamingGroup) {
        mAccountLocalRepo.setSelectedGamingGroupId(gamingGroup.getServerId());
    }

    public String getSelectedGamingGroupServerId() {
        return mAccountLocalRepo.getSelectedGamingGroupServerId();
    }

    public long getLastSyncDate() {
        return mAccountLocalRepo.getLastSyncDate();
    }

    public void saveLastSyncDate(long timestamp) {
        mAccountLocalRepo.setLastSyncDate(timestamp);
    }

    public String getAccountUserName() {
        return mAccountLocalRepo.getUserName();
    }

    public String getUserId() {
        return mAccountLocalRepo.getUserId();
    }
}
