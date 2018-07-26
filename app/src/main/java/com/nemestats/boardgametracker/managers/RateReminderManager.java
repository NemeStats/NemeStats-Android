package com.nemestats.boardgametracker.managers;


import com.nemestats.boardgametracker.dal.local.RateReminderRepository;

import javax.inject.Inject;

/**
 * Created by mehegeo on 11/24/17.
 */

public class RateReminderManager {

    private RateReminderRepository mRateReminderRepository;

    @Inject
    RateReminderManager(RateReminderRepository rateReminderRepository) {
        mRateReminderRepository = rateReminderRepository;
    }

    public void incrementCountAppOpened() {
        mRateReminderRepository.incrementCountAppOpened();
    }

    public int getAppOpenedCount() {
        return mRateReminderRepository.getAppOpenedCount();
    }

    public void setAppRated(boolean appRated) {
        mRateReminderRepository.setAppRated(appRated);
    }

    public boolean getAppRated() {
        return mRateReminderRepository.getAppRated();
    }


}
