package com.nemestats.boardgametracker.webservices.interceptor;

import com.nemestats.boardgametracker.dal.local.AccountLocalRepo;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mehegeo on 9/23/17.
 */

public class NemeStatsInterceptor implements Interceptor {
    private static final String KEY_HEADER_CONTENT_TYPE = "Content-Type";
    private static final String VALUE_HEADER_CONTENT_TYPE = "application/json";

    private AccountLocalRepo mAccountLocalRepo;

    @Inject
    public NemeStatsInterceptor() {
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder signedRequest = chain.request().newBuilder();
        signedRequest.addHeader(KEY_HEADER_CONTENT_TYPE, VALUE_HEADER_CONTENT_TYPE);

        return chain.proceed(signedRequest.build());
    }
}
