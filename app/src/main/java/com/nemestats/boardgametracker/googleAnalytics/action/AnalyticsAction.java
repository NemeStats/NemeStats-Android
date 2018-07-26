package com.nemestats.boardgametracker.googleAnalytics.action;

public class AnalyticsAction {

    private String mLocation, mTrigger;
    public static final String SEPARATOR = "|";


    public AnalyticsAction(String location, String trigger) {
        mLocation = location;
        mTrigger = trigger;
    }

    @Override
    public String toString() {
        String result = "";

        if (mLocation != null) {
            result += mLocation.trim();
        }
        result += SEPARATOR;
        if (mTrigger != null) {
            result += mTrigger.trim();
        }
        return result;
    }
}
