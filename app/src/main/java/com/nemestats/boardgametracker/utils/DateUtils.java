package com.nemestats.boardgametracker.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Created by mehegeo on 10/15/17.
 */

public class DateUtils {

    public static boolean isToday(DateTime dateTime) {
        DateTime today = DateTime.now();

        if (dateTime.getYear() == today.getYear()) {
            if (dateTime.getMonthOfYear() == today.getMonthOfYear()) {
                if (dateTime.getDayOfMonth() == today.getDayOfMonth()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isYesterday(DateTime dateTime) {
        DateTime yesterday = DateTime.now().minusDays(1);

        if (dateTime.getYear() == yesterday.getYear()) {
            if (dateTime.getMonthOfYear() == yesterday.getMonthOfYear()) {
                if (dateTime.getDayOfMonth() == yesterday.getDayOfMonth()) {
                    return true;
                }
            }
        }
        return false;
    }

}
