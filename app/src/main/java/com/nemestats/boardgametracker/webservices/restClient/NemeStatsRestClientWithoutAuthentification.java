package com.nemestats.boardgametracker.webservices.restClient;

import com.nemestats.boardgametracker.webservices.interceptor.NemeStatsInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by mehegeo on 9/23/17.
 */

public class NemeStatsRestClientWithoutAuthentification {


    private OkHttpClient mOkHttpClient;

    @Inject
    public NemeStatsRestClientWithoutAuthentification(NemeStatsInterceptor nemeStatsInterceptor) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(nemeStatsInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
