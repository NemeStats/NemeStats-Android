package com.nemestats.boardgametracker.dagger;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mehegeo on 9/23/17.
 */

@Module
public class AppModule {
    private Context mContext;

    @Provides
    public Context getContext() {
        return mContext;
    }

    public AppModule(Context context) {
        mContext = context;
    }

}
