package com.nemestats.boardgametracker;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.nemestats.boardgametracker.dagger.AppComponent;
import com.nemestats.boardgametracker.dagger.AppGraph;

import io.fabric.sdk.android.Fabric;

/**
 * Created by mehegeo on 9/23/17.
 */

public class NemeStatsApplication extends Application {

    private static AppGraph mAppGraph;
    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppGraph = AppComponent.Initializer.init(this);

        if (BuildConfig.CRASHLYTICS_ENABLED) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Fabric.with(this, new Crashlytics());
        }
    }

    public static FirebaseAnalytics getFirebaseAnalyticsInstance() {
        return mFirebaseAnalytics;
    }

    public static AppGraph getAppGraph() {
        return mAppGraph;
    }
}
