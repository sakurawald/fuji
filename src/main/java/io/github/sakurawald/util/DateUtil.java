package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;

@UtilityClass
public class DateUtil {

    private static final SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toStandardDateFormat(long timeMillis) {
        return STANDARD_DATE_FORMAT.format(timeMillis);
    }
}
