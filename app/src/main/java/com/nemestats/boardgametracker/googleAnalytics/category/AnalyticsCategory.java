package com.nemestats.boardgametracker.googleAnalytics.category;

public class AnalyticsCategory {

    private AnalyticsCategoryType mAnalyticsCategoryType;
    private String mDescription;

    public static final String SEPARATOR = "|";


    public AnalyticsCategory(AnalyticsCategoryType analyticsCategoryType, String description) {
        mAnalyticsCategoryType = analyticsCategoryType;
        mDescription = description;
    }

    @Override
    public String toString() {
        return mAnalyticsCategoryType.name() + SEPARATOR + mDescription;
    }
}
