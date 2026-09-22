package cn.master.nuts.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author : 11's papa
 * @since : 2026/9/22, 星期二
 **/
public class DateUtils {
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String COMPACT__PATTERN = "yyyyMMddHHmmss";

    private DateUtils() {
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parse(String dateTimeStr, String pattern) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
}
