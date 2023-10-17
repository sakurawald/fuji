package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;

@UtilityClass
public class TimeUtil {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFormattedDate(long timeMillis) {
        return SDF.format(timeMillis);
    }
}
