package com.nemestats.boardgametracker.dal.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

/**
 * Created by mehegeo on 9/23/17.
 */

public class AccountLocalRepo {

    public static final String AUTHENTICATION_TOKEN = "authenticationToken";
    public static final String AUTHENTICATION_TOKEN_EXPIRATION_DATE = "authenticationTokenExpirationDateTime";
    private static final String USER_NAME = "username";
    private static final String USER_PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String USER_ID = "userId";
    private static final String PLAYER_ID = "playerId";
    private static final String PLAYER_NAME = "playerName";
    private static final String GAMING_GROUP_ID = "gamingGroupId";
    private static final String GAMING_GROUP_NAME = "gamingGroupName";
    public static final String SELECTED_GAMING_GROUP_SERVER_ID = "selecteGamingGroupServerId";
    public static final String LAST_SYNC_DATE = "lastSyncDate";

    private SharedPreferences mSharedPreferences;

    @Inject
    public AccountLocalRepo(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public long getLastSyncDate() {
        return mSharedPreferences.getLong(LAST_SYNC_DATE, 0);
    }


    @SuppressLint("ApplySharedPref")
    public void setLastSyncDate(long timestamp) {
        mSharedPreferences.edit().putLong(LAST_SYNC_DATE, timestamp).commit();
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    public void saveAuthenticationToken(String authenticationToken) {
        mSharedPreferences.edit().putString(AUTHENTICATION_TOKEN, authenticationToken).commit();
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    public void saveAuthenticationTokenExpirationDate(String authenticationTokenExpirationDate) {
        mSharedPreferences.edit().putString(AUTHENTICATION_TOKEN_EXPIRATION_DATE, authenticationTokenExpirationDate).commit();
    }

    public String getAuthenticationToken() {
        return mSharedPreferences.getString(AUTHENTICATION_TOKEN, null);
    }

    public String getAuthenticationTokenExpirationDate() {
        return mSharedPreferences.getString(AUTHENTICATION_TOKEN_EXPIRATION_DATE, null);
    }

    public boolean isUserLogged() {
        return getAuthenticationToken() != null && getAuthenticationTokenExpirationDate() != null;
    }

    @SuppressLint("ApplySharedPref")
    public void deleteAll() {
        mSharedPreferences.edit().clear().commit();
    }

    @SuppressLint("ApplySharedPref")
    public void saveUsername(String userName) {
        mSharedPreferences.edit().putString(USER_NAME, userName).commit();
    }

    public String getUserName() {
        return mSharedPreferences.getString(USER_NAME, null);
    }

    @SuppressLint("ApplySharedPref")
    public void saveUserPassword(String userPassword) {
        mSharedPreferences.edit().putString(USER_PASSWORD, userPassword).commit();
    }

    public String getUserPassword() {
        return mSharedPreferences.getString(USER_PASSWORD, null);
    }

    @SuppressLint("ApplySharedPref")
    public void saveEmail(String email) {
        mSharedPreferences.edit().putString(EMAIL, email).commit();
    }

    public String getEmail() {
        return mSharedPreferences.getString(EMAIL, null);
    }

    @SuppressLint("ApplySharedPref")
    public void saveUserId(String userId) {
        mSharedPreferences.edit().putString(USER_ID, userId).commit();
    }

    public String getUserId() {
        return mSharedPreferences.getString(USER_ID, null);
    }

    @SuppressLint("ApplySharedPref")
    public void savePlayerId(int playerId) {
        mSharedPreferences.edit().putInt(PLAYER_ID, playerId).commit();
    }

    public int getPlayerId() {
        return mSharedPreferences.getInt(PLAYER_ID, -1);
    }

    @SuppressLint("ApplySharedPref")
    public void savePlayerName(String playerName) {
        mSharedPreferences.edit().putString(PLAYER_NAME, playerName).commit();
    }

    public String getPlayerName() {
        return mSharedPreferences.getString(PLAYER_NAME, null);
    }

    @SuppressLint("ApplySharedPref")
    public void saveGamingGroupId(int gamingGroupId) {
        mSharedPreferences.edit().putInt(GAMING_GROUP_ID, gamingGroupId).commit();
    }

    public int getGamingGroupId() {
        return mSharedPreferences.getInt(GAMING_GROUP_ID, -1);
    }

    @SuppressLint("ApplySharedPref")
    public void saveGamingGroupName(String gamingGroupName) {
        mSharedPreferences.edit().putString(GAMING_GROUP_NAME, gamingGroupName).commit();
    }

    public String getGamingGroupName() {
        return mSharedPreferences.getString(GAMING_GROUP_NAME, null);
    }

    @SuppressLint("ApplySharedPref")
    public void setSelectedGamingGroupId(String gamingGroupServerId) {
        mSharedPreferences.edit().putString(SELECTED_GAMING_GROUP_SERVER_ID, gamingGroupServerId).commit();
    }

    public String getSelectedGamingGroupServerId() {
        return mSharedPreferences.getString(SELECTED_GAMING_GROUP_SERVER_ID, null);
    }
}
