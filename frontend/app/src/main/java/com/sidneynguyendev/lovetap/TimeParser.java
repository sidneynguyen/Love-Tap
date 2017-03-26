package com.sidneynguyendev.lovetap;

/**
 * File Name: TimeParser.java
 * Authors: Sidney Nguyen
 * Date Created: 3/26/17.
 */

class TimeParser {
    private static final long MILLIS_IN_A_SEC = 1000;
    private static final long SECS_IN_A_HOUR = 3600;
    private static final long SECS_IN_A_MIN = 60;
    private static final long DOUBLE_DIGITS = 10;

    static String parseMillis(long millis) {
        long time = millis/MILLIS_IN_A_SEC;
        long hours = time/SECS_IN_A_HOUR;
        time %= SECS_IN_A_HOUR;
        long mins = time/SECS_IN_A_MIN;
        time %= SECS_IN_A_MIN;
        long secs = time;
        String hour;
        String min;
        String sec;
        if (hours < DOUBLE_DIGITS) {
            hour = "0" + hours;
        } else {
            hour = "" + hours;
        }
        if (mins < DOUBLE_DIGITS) {
            min = "0" + mins;
        } else {
            min = "" + mins;
        }
        if (secs < DOUBLE_DIGITS) {
            sec = "0" + secs;
        } else {
            sec = "" + secs;
        }
        return hour + ":" + min + ":" + sec;
    }
}
