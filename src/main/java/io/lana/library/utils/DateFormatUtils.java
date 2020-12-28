package io.lana.library.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtils {
    public static final DateTimeFormatter COMMON_DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd[ HH:mm:ss]");

    private static final DateTimeFormatter COMMON_DATE_FORMAT_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyy-MM-dd");

    private DateFormatUtils() {
    }

    public static String toDateStringWithDefaultUnknown(LocalDate localDate) {
        if (localDate == null) {
            return "Unknown";
        }
        return localDate.format(COMMON_DATE_FORMAT_WITHOUT_TIME);
    }

    public static String toDateStringWithDefaultUnknown(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "Unknown";
        }
        return localDateTime.format(COMMON_DATE_FORMAT);
    }
}
