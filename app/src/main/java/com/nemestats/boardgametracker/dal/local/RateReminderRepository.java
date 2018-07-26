package com.nemestats.boardgametracker.dal.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

/**
 * Created by mehegeo on 11/24/17.
 */

public class RateReminderRepository {

    private final Context mContext;
    private final SharedPreferences mSharedPreferences;

    public final String COUNT_APP_OPENED = "countAppOpened";
    public final String APP_RATED = "appRated";


    @Inject
    public RateReminderRepository(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void incrementCountAppOpened() {
        int appOpenedCount = getAppOpenedCount();
        mSharedPreferences.edit().putInt(COUNT_APP_OPENED, appOpenedCount + 1).apply();
    }

    public int getAppOpenedCount() {
        return mSharedPreferences.getInt(COUNT_APP_OPENED, 0);
    }

    public void setAppRated(boolean appRated) {
        mSharedPreferences.edit().putBoolean(APP_RATED, appRated).apply();
    }

    public boolean getAppRated() {
        return mSharedPreferences.getBoolean(APP_RATED, false);
    }


}
