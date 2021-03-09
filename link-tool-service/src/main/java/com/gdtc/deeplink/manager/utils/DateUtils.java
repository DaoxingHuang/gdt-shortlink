package com.gdtc.deeplink.manager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    private static final SimpleDateFormat dateHourFormat = new SimpleDateFormat("MMddHHmm");

    public synchronized static String getDateHour(Date date) {
        if (null == date) {
            date = new Date();
        }
        return dateHourFormat.format(date);
    }
}
