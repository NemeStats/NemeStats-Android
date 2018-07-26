package com.nemestats.boardgametracker.webservices.restClient;

import com.nemestats.boardgametracker.webservices.interceptor.NemeStatsAuthenticationInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mehegeo on 9/23/17.
 */

public class NemeStatsRestClientWithAuthentification {

    private OkHttpClient mOkHttpClient;

    @Inject
    public NemeStatsRestClientWithAuthentification(NemeStatsAuthenticationInterceptor nemeStatsAuthenticationInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(nemeStatsAuthenticationInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
