package com.nemestats.boardgametracker.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by geomehedeniuc on 5/3/18.
 */

public class UserSessionExpiredBroadcastReceiver extends BroadcastReceiver {

    public static final String USER_SESSION_EXPIRED = "userSessionExpired";

    private OnDataSyncFinishedListener mOnDataSyncFinishedListener;

    public UserSessionExpiredBroadcastReceiver(OnDataSyncFinishedListener onDataSyncFinishedListener) {
        mOnDataSyncFinishedListener = onDataSyncFinishedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (USER_SESSION_EXPIRED.equals(intent.getAction())) {
            if (mOnDataSyncFinishedListener != null) {
                mOnDataSyncFinishedListener.onUserSessionExpired();
            }
        }
    }

    public interface OnDataSyncFinishedListener {
        void onUserSessionExpired();
    }
}
