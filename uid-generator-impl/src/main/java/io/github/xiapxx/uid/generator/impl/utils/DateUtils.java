package io.github.xiapxx.uid.generator.impl.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author xiapeng
 * @Date 2025-03-06 14:52
 */
public class DateUtils {

    private static final SimpleDateFormat YYYY_MM_DD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parseDay(String str) {
        try {
            return YYYY_MM_DD_FORMAT.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }
}
