package com.nemestats.boardgametracker.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.nemestats.boardgametracker.broadcasts.UserSessionExpiredBroadcastReceiver;

/**
 * Created by geomehedeniuc on 5/3/18.
 */

public class SessionExpiredBaseActivity extends AppCompatActivity {

    private UserSessionExpiredBroadcastReceiver mUserSessionExpiredBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserSessionExpiredBroadcastReceiver = new UserSessionExpiredBroadcastReceiver(this::finishActivityAndRedirectToLogin);

        IntentFilter userSessionExpiredIntent = new IntentFilter();
        userSessionExpiredIntent.addAction(UserSessionExpiredBroadcastReceiver.USER_SESSION_EXPIRED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mUserSessionExpiredBroadcastReceiver, userSessionExpiredIntent);
    }

    protected void finishActivityAndRedirectToLogin() {
        Intent intent = new Intent(this, MainActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mUserSessionExpiredBroadcastReceiver);
    }
}
