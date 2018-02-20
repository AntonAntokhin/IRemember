package com.antohin.iremember.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class Utils {

    public static String milSekToMInSek(int milliseconds) {
        SimpleDateFormat dateFormat;
        if (milliseconds < 3_600_000) {
            dateFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
        final int ROUND_OFF = 500;
        return dateFormat.format(milliseconds + ROUND_OFF);
    }
}
