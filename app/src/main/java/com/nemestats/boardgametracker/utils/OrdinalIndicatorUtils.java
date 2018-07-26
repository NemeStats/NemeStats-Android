package com.nemestats.boardgametracker.utils;

/**
 * Created by mehegeo on 10/16/17.
 */

public class OrdinalIndicatorUtils {
    public static String formatOrdinalIndicatorForNumber(final int n) {
        if (n >= 11 && n <= 13) {
            return "<big>" + n + "</big>" + "<sup><small>th</small></sup>";
        }
        switch (n % 10) {
            case 1:
                return "<b><big>" + n + "</big>" + "<sup><small>st</small></sup></b>";
            case 2:
                return "<big>" + n + "</big>" + "<sup><small>nd</small></sup>";
            case 3:
                return "<big>" + n + "</big>" + "<sup><small>rd</small></sup>";
            default:
                return "<big>" + n + "</big>" + "<sup><small>th</small></sup>";
        }
    }

}
