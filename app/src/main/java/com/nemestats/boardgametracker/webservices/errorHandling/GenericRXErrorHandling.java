package com.nemestats.boardgametracker.webservices.errorHandling;

import android.content.Context;

import com.nemestats.boardgametracker.R;
import com.nemestats.boardgametracker.webservices.dto.GenericErrorDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Created by mehegeo on 9/23/17.
 */

public class GenericRXErrorHandling {

    public static String extractErrorMessage(Context context, Throwable throwable) {
        String defaultMessage = context.getString(R.string.txt_network_default_error_message);

        if (throwable instanceof HttpException) {
            // We had non-2XX http error
            try {
                String errorMessage = parseError(((HttpException) throwable).response());
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    return errorMessage;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (throwable instanceof IOException) {
            // A network or conversion error happened
            return context.getString(R.string.txt_no_internet);
        }

        return defaultMessage;
    }

    private static String parseError(Response<?> response) throws Exception {
        String errorJsonRaw = response.errorBody().string();
        if (errorJsonRaw != null) {

            Gson gson = new Gson();

            return gson.fromJson(errorJsonRaw, GenericErrorDTO.class).getMessage();
        }
        return null;
    }
}
