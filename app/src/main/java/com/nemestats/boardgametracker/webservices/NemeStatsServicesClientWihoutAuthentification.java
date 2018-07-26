package com.nemestats.boardgametracker.webservices;

import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateAccountRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateAccountResponseDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateUserSessionRequestDTO;
import com.nemestats.boardgametracker.webservices.dto.userAccount.CreateUserSessionResponseDTO;
import com.nemestats.boardgametracker.webservices.restClient.NemeStatsRestClientWithoutAuthentification;

import javax.inject.Inject;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mehegeo on 9/23/17.
 */

public class NemeStatsServicesClientWihoutAuthentification {

    private Retrofit mRetrofitClient;
    private NemeStatsService mNemeStatsService;

    @Inject
    public NemeStatsServicesClientWihoutAuthentification(NemeStatsRestClientWithoutAuthentification nemeStatsRestClient) {
        mRetrofitClient = new Retrofit.Builder()
                .baseUrl(RestConfig.ENPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(nemeStatsRestClient.getOkHttpClient())
                .build();
    }

    public NemeStatsService getService() {
        if (mNemeStatsService == null) {
            mNemeStatsService = mRetrofitClient.create(NemeStatsService.class);
        }
        return mNemeStatsService;
    }

    public interface NemeStatsService {

        @POST(value = "UserSessions")
        Observable<CreateUserSessionResponseDTO> createUserSession(@Body CreateUserSessionRequestDTO createUserSessionRequestDTO);

        @POST(value = "Users")
        Observable<CreateAccountResponseDTO> createAccount(@Body CreateAccountRequestDTO createAccountRequestDTO);
    }
}
