package fun.sakurawald.util;

import java.text.SimpleDateFormat;

public class TimeUtil {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFormattedDate(long timeMillis) {
        return SDF.format(timeMillis);
    }
}
