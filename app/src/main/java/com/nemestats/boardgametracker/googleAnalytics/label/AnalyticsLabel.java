package com.nemestats.boardgametracker.googleAnalytics.label;

import java.util.LinkedHashMap;
import java.util.Map;

public class AnalyticsLabel {

    public static final String SEPARATOR = "|";

    private LinkedHashMap<String, String> mKeys;


    public AnalyticsLabel(LinkedHashMap<String, String> keys) {
        mKeys = keys;
    }

    @Override
    public String toString() {
        String result = "";

        if (mKeys != null && !mKeys.isEmpty()) {
            for (Map.Entry<String, String> entry : mKeys.entrySet()) {

                if (entry.getValue() != null && !result.isEmpty()) {
                    result += SEPARATOR;
                }

                if (entry.getValue() != null) {
                    result += entry.getKey().trim() + "=" + entry.getValue().trim();
                }
            }
        }


        return result;
    }
}
