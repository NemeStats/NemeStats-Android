package com.nemestats.boardgametracker.googleAnalytics;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nemestats.boardgametracker.NemeStatsApplication;
import com.nemestats.boardgametracker.googleAnalytics.action.AnalyticsAction;
import com.nemestats.boardgametracker.googleAnalytics.category.AnalyticsCategory;
import com.nemestats.boardgametracker.googleAnalytics.label.AnalyticsLabel;
import com.nemestats.boardgametracker.managers.AccountManager;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class FirebaseAnalyticsLogger {

    private AccountManager mAccountManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Inject
    public FirebaseAnalyticsLogger(AccountManager accountManager) {
        mAccountManager = accountManager;
        mFirebaseAnalytics = NemeStatsApplication.getFirebaseAnalyticsInstance();

    }

    public void logEvent(AnalyticsCategory analyticsCategory, AnalyticsAction analyticsAction, AnalyticsLabel analyticsLabel) {
        if (mFirebaseAnalytics != null) {
            if (mAccountManager.isUserLoggedIn()) {

                String accountId = String.valueOf(mAccountManager.getUserId());
                mFirebaseAnalytics.setUserId(accountId);
            } else {
                mFirebaseAnalytics.setUserId(null);
            }

            Bundle params = new Bundle();
            params.putString("category", analyticsCategory.toString());
            params.putString("action", analyticsAction.toString());
            params.putString("label", analyticsLabel.toString());

            mFirebaseAnalytics.logEvent("ga_event", params);
        }
    }
}
