package com.nemestats.boardgametracker.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by geomehedeniuc on 4/28/18.
 */

public class DataSyncBroadcastReceiver extends BroadcastReceiver {

    public static final String DATA_SYNC_FINISHED_SUCCESS = "dataSyncFinishedSuccess";
    public static final String FORCE_UPDATE_DATA = "forceUpdateData";
    public static final String DATA_SYNC_FINISHED_FAIL = "dataSyncFinishedFail";

    private OnDataSyncFinishedListener mOnDataSyncFinishedListener;

    public DataSyncBroadcastReceiver(OnDataSyncFinishedListener onDataSyncFinishedListener) {
        mOnDataSyncFinishedListener = onDataSyncFinishedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DATA_SYNC_FINISHED_SUCCESS.equals(intent.getAction())) {
            if (mOnDataSyncFinishedListener != null) {
                mOnDataSyncFinishedListener.onDataSyncFinishedSuccess();
            }
        } else if (DATA_SYNC_FINISHED_FAIL.equals(intent.getAction())) {
            if (mOnDataSyncFinishedListener != null) {
                mOnDataSyncFinishedListener.onDataSyncFinishedFail();
            }
        } else if (FORCE_UPDATE_DATA.equals(intent.getAction())) {
            if (mOnDataSyncFinishedListener != null) {
                mOnDataSyncFinishedListener.onForceUpdateData();
            }
        }
    }

    public interface OnDataSyncFinishedListener {
        void onDataSyncFinishedSuccess();

        void onDataSyncFinishedFail();

        void onForceUpdateData();
    }
}
